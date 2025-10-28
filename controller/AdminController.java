package controller;

import view.AdminDashboardView;
import model.Movie;
import model.MovieDAO;
import model.MovieDAOImpl;
import model.Show;
import model.ShowDAO;
import model.ShowDAOImpl;
import model.Booking;
import model.BookingDAO;
import model.BookingDAOImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 
import java.time.format.DateTimeParseException; 

public class AdminController {

    private AdminDashboardView view;
    private MovieDAO movieDAO;
    private ShowDAO showDAO;
    private BookingDAO bookingDAO;

    // DateTimeFormatter for parsing and displaying timestamps in a specific format
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    public AdminController(AdminDashboardView view) {
        this.view = view;

        try {
            this.movieDAO = new MovieDAOImpl();
            this.showDAO = new ShowDAOImpl();
            this.bookingDAO = new BookingDAOImpl();

            addListeners(); // All listeners added here

            // Initial data loads
            loadMoviesIntoTable();
            loadMoviesIntoDropdown();
            loadShowsIntoTable();
            loadBookingsIntoTable();

        } catch (Exception e) {
            e.printStackTrace();
            // Centralized error reporting for initialization issues
            showError("Could not open Admin Dashboard. Please check database connection.\n\nError: " + e.getMessage(), "Admin Panel Error");
            view.dispose(); // Close the dashboard if DB connection fails
        }
    }

    private void addListeners() {
        // --- Movie Tab Listeners ---
        view.getAddMovieButton().addActionListener(e -> onAddMovie());
        view.getUpdateMovieButton().addActionListener(e -> onUpdateMovie());
        view.getDeleteMovieButton().addActionListener(e -> onDeleteMovie());
        view.getClearMovieFormButton().addActionListener(e -> onClearForm());
        view.getMovieTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onMovieTableSelect();
            }
        });

        // --- Show Tab Listeners ---
        view.getAddShowButton().addActionListener(e -> onAddShow());
        view.getDeleteShowButton().addActionListener(e -> onDeleteShow());
        view.getShowTable().getSelectionModel().addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) {
                 boolean rowSelected = view.getShowTable().getSelectedRow() != -1;
                 view.getDeleteShowButton().setEnabled(rowSelected);
             }
        });

        // --- Booking Tab Listeners ---
        view.getDeleteBookingButton().addActionListener(e -> onDeleteBooking());
        view.getBookingTable().getSelectionModel().addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) {
                 boolean rowSelected = view.getBookingTable().getSelectedRow() != -1;
                 view.getDeleteBookingButton().setEnabled(rowSelected);
             }
        });
    }

    // --- MOVIE MANAGEMENT ---

    private void loadMoviesIntoTable() {
        DefaultTableModel model = view.getMovieTableModel();
        model.setRowCount(0); // Clear existing data

        try {
            List<Movie> movies = movieDAO.getAllMovies();
            for (Movie movie : movies) {
                model.addRow(new Object[]{
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getGenre(),
                    movie.getDuration(),
                    movie.getPosterPath()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading movies: " + e.getMessage());
        }
    }

    // Helper method to get Movie data from the form fields
    private Movie getMovieFromForm() throws NumberFormatException, IllegalArgumentException {
        int id = -1; // Default for new movie, -1 indicates no ID yet
        String idText = view.getMovieIdField().getText();
        if (!idText.isEmpty()) {
            id = Integer.parseInt(idText);
        }

        String title = view.getMovieTitleField().getText().trim();
        String genre = view.getMovieGenreField().getText().trim();
        String durationText = view.getMovieDurationField().getText().trim();
        String posterPath = view.getMoviePosterField().getText().trim();

        if (title.isEmpty() || genre.isEmpty() || durationText.isEmpty()) {
            throw new IllegalArgumentException("Title, Genre, and Duration cannot be empty.");
        }
        
        int duration = Integer.parseInt(durationText); // Throws NumberFormatException if invalid

        if (id != -1) {
            return new Movie(id, title, genre, duration, posterPath);
        } else {
            return new Movie(title, genre, duration, posterPath);
        }
    }

    private void onMovieTableSelect() {
        int selectedRow = view.getMovieTable().getSelectedRow();
        if (selectedRow == -1) {
            onClearForm(); // Clear form if no row is selected
            return;
        }

        DefaultTableModel model = view.getMovieTableModel();
        view.getMovieIdField().setText(model.getValueAt(selectedRow, 0).toString());
        view.getMovieTitleField().setText(model.getValueAt(selectedRow, 1).toString());
        view.getMovieGenreField().setText(model.getValueAt(selectedRow, 2).toString());
        view.getMovieDurationField().setText(model.getValueAt(selectedRow, 3).toString());
        view.getMoviePosterField().setText(model.getValueAt(selectedRow, 4) != null ? model.getValueAt(selectedRow, 4).toString() : "");

        // Adjust button states
        view.getAddMovieButton().setEnabled(false);
        view.getUpdateMovieButton().setEnabled(true);
        view.getDeleteMovieButton().setEnabled(true);
    }

    private void onClearForm() {
        view.getMovieIdField().setText("");
        view.getMovieTitleField().setText("");
        view.getMovieGenreField().setText("");
        view.getMovieDurationField().setText("");
        view.getMoviePosterField().setText("");

        // Reset button states
        view.getAddMovieButton().setEnabled(true);
        view.getUpdateMovieButton().setEnabled(false);
        view.getDeleteMovieButton().setEnabled(false);
        view.getMovieTable().clearSelection(); // Clear table selection
    }

    private void onAddMovie() {
        try {
            Movie movie = getMovieFromForm(); // Use helper to get movie data
            // ID will be -1 here as we expect a new movie

            boolean success = movieDAO.addMovie(movie);

            if (success) {
                showMessage("Movie added successfully!");
                loadMoviesIntoTable();
                loadMoviesIntoDropdown(); // Refresh dropdown for new show creation
                onClearForm();
            } else {
                showError("Failed to add movie.");
            }
        } catch (NumberFormatException ex) {
            showError("Duration must be a valid number (e.g., 120).");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage()); // Catches empty title/genre/duration
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Database error: " + ex.getMessage());
        }
    }

    private void onUpdateMovie() {
        try {
            Movie movie = getMovieFromForm(); // Use helper to get movie data
            // Expect movie.getId() to be a valid ID here

            if (movie.getMovieId() == -1) { // Check if ID was actually parsed (shouldn't happen with button disabled)
                showError("No movie selected for update.");
                return;
            }

            boolean success = movieDAO.updateMovie(movie);

            if (success) {
                showMessage("Movie updated successfully!");
                loadMoviesIntoTable();
                loadMoviesIntoDropdown(); // Refresh dropdown in case movie title changed
                onClearForm();
            } else {
                showError("Failed to update movie.");
            }
        } catch (NumberFormatException ex) {
            showError("ID and Duration must be valid numbers.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Database error: " + ex.getMessage());
        }
    }

    private void onDeleteMovie() {
        try {
            int id = Integer.parseInt(view.getMovieIdField().getText());

            int choice = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to delete this movie?\nThis will also delete associated shows and bookings.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                boolean success = movieDAO.deleteMovie(id);
                if (success) {
                    showMessage("Movie deleted successfully!");
                    loadMoviesIntoTable();
                    loadMoviesIntoDropdown();
                    loadShowsIntoTable(); // Shows related to this movie are also deleted
                    loadBookingsIntoTable(); // Bookings related to this movie are also deleted
                    onClearForm();
                } else {
                    showError("Failed to delete movie.");
                }
            }
        } catch (NumberFormatException ex) {
            showError("No movie selected or invalid ID.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (ex.getMessage().contains("foreign key constraint fails")) {
                showError("Cannot delete movie. It is currently referenced by existing shows or bookings (though cascade delete should handle shows and bookings, if configured correctly in DB).");
            } else {
                showError("Database error: " + ex.getMessage());
            }
        }
    }

    // --- SHOW MANAGEMENT ---

    private void loadMoviesIntoDropdown() {
        try {
            view.getMovieDropdown().removeAllItems();
            List<Movie> movies = movieDAO.getAllMovies();
            for (Movie movie : movies) {
                view.getMovieDropdown().addItem(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading movies into dropdown.");
        }
    }

    private void loadShowsIntoTable() {
        DefaultTableModel model = view.getShowTableModel();
        model.setRowCount(0);

        try {
            List<Show> shows = showDAO.getAllShows();
            for (Show show : shows) {
                model.addRow(new Object[]{
                    show.getShowId(),
                    show.getMovieTitle(), // Assumes Show object has this field from a JOIN
                    show.getScreenId(),
                    DISPLAY_DATE_FORMAT.format(show.getShowTime()), // Formatted for display
                    String.format("%.2f", show.getPrice())
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading shows: " + e.getMessage());
        }
    }

    // Helper method to get Show data from the form fields
    private Show getShowDetailsFromForm() throws NumberFormatException, IllegalArgumentException, DateTimeParseException {
        Movie selectedMovie = (Movie) view.getMovieDropdown().getSelectedItem();
        if (selectedMovie == null) {
            throw new IllegalArgumentException("Please select a movie.");
        }
        int movieId = selectedMovie.getMovieId();

        String screenText = view.getScreenField().getText().trim();
        String priceText = view.getPriceField().getText().trim();
        String timeString = view.getShowTimeField().getText().trim();

        if (screenText.isEmpty() || priceText.isEmpty() || timeString.isEmpty()) {
             throw new IllegalArgumentException("Screen ID, Price, and Show Time cannot be empty.");
        }

        int screenId = Integer.parseInt(screenText); // Throws NumberFormatException
        double price = Double.parseDouble(priceText); // Throws NumberFormatException

        // Use LocalDateTime for parsing, then convert to Timestamp
        LocalDateTime localDateTime = LocalDateTime.parse(timeString, DATETIME_FORMATTER); // Throws DateTimeParseException
        Timestamp showTime = Timestamp.valueOf(localDateTime);

        return new Show(movieId, screenId, showTime, price);
    }


    private void onAddShow() {
        try {
            Show show = getShowDetailsFromForm(); // Use helper to get show data

            boolean success = showDAO.addShow(show);

            if (success) {
                showMessage("Show added successfully!");
                loadShowsIntoTable();
                loadBookingsIntoTable(); // A new show might create possibilities for bookings
                // Clear form fields after successful add
                view.getScreenField().setText("");
                view.getShowTimeField().setText("");
                view.getPriceField().setText("");
            } else {
                showError("Failed to add show.");
            }
        } catch (NumberFormatException ex) {
            showError("Screen and Price must be valid numbers.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage()); // Catches empty fields or no movie selected
        } catch (DateTimeParseException ex) {
             showError("Invalid Showtime format. Use YYYY-MM-DD HH:MM (e.g., 2023-10-27 14:30)");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Database error: " + ex.getMessage());
        }
    }

    private void onDeleteShow() {
        int selectedRow = view.getShowTable().getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a show from the table to delete.");
            return;
        }

        int showId = (int) view.getShowTableModel().getValueAt(selectedRow, 0);

        int choice = JOptionPane.showConfirmDialog(
            view,
            "Are you sure you want to delete this show?\nThis may also delete associated bookings.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = showDAO.deleteShow(showId);
                if (success) {
                    showMessage("Show deleted successfully!");
                    loadShowsIntoTable();
                    loadBookingsIntoTable(); // Bookings for this show might be deleted
                    view.getDeleteShowButton().setEnabled(false);
                } else {
                    showError("Failed to delete show.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                if (ex.getMessage().contains("foreign key constraint fails")) {
                    showError("Cannot delete show. It has associated bookings.\nDelete associated bookings first (if cascade delete is not set).");
                } else {
                    showError("Database error: " + ex.getMessage());
                }
            }
        }
    }

    // --- BOOKING MANAGEMENT ---

    private void loadBookingsIntoTable() {
        DefaultTableModel model = view.getBookingTableModel();
        model.setRowCount(0);

        try {
            List<Booking> bookings = bookingDAO.getAllBookingsDetailed();
            for (Booking booking : bookings) {
                model.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getCustomerName(),
                    booking.getCustomerPhone(),
                    booking.getMovieTitle(), // Assumes Booking object has this from a JOIN
                    booking.getFormattedShowTime(), // Assumes Booking object handles formatting
                    booking.getSelectedSeats(),
                    String.format("%.2f", booking.getTotalAmount()),
                    booking.getFormattedBookingTime() // Assumes Booking object handles formatting
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading bookings: " + e.getMessage());
        }
    }

    private void onDeleteBooking() {
        int selectedRow = view.getBookingTable().getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking from the table to delete.");
            return;
        }

        int bookingId = (int) view.getBookingTableModel().getValueAt(selectedRow, 0);

        int choice = JOptionPane.showConfirmDialog(
            view,
            "Are you sure you want to delete this booking?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = bookingDAO.deleteBooking(bookingId);
                if (success) {
                    showMessage("Booking deleted successfully!");
                    loadBookingsIntoTable(); // Refresh table
                    view.getDeleteBookingButton().setEnabled(false);
                } else {
                    showError("Failed to delete booking.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showError("Database error: " + ex.getMessage());
            }
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(view, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
