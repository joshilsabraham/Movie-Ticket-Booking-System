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

public class AdminController {

    private AdminDashboardView view;
    private MovieDAO movieDAO;
    private ShowDAO showDAO;
    private BookingDAO bookingDAO; 

    public AdminController(AdminDashboardView view) {
        this.view = view;

        try {
            this.movieDAO = new MovieDAOImpl();
            this.showDAO = new ShowDAOImpl();
            this.bookingDAO = new BookingDAOImpl(); 

            addListeners();

            loadMoviesIntoTable();
            loadMoviesIntoDropdown();
            loadShowsIntoTable();
            loadBookingsIntoTable(); 

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                view.getParent(),
                "Could not open Admin Dashboard. Please check database connection.\n\nError: " + e.getMessage(),
                "Admin Panel Error",
                JOptionPane.ERROR_MESSAGE
            );
            view.dispose();
        }
    }

    private void addListeners() {
        view.getAddMovieButton().addActionListener(e -> onAddMovie());
        view.getUpdateMovieButton().addActionListener(e -> onUpdateMovie());
        view.getDeleteMovieButton().addActionListener(e -> onDeleteMovie());
        view.getClearMovieFormButton().addActionListener(e -> onClearForm());
        view.getMovieTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onMovieTableSelect();
            }
        });

        view.getAddShowButton().addActionListener(e -> onAddShow());
        view.getDeleteShowButton().addActionListener(e -> onDeleteShow());
        view.getShowTable().getSelectionModel().addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) {
                 boolean rowSelected = view.getShowTable().getSelectedRow() != -1;
                 view.getDeleteShowButton().setEnabled(rowSelected);
            }
        });

        view.getDeleteBookingButton().addActionListener(e -> onDeleteBooking()); // Added
        view.getBookingTable().getSelectionModel().addListSelectionListener(e -> { // Added
             if (!e.getValueIsAdjusting()) {
                 boolean rowSelected = view.getBookingTable().getSelectedRow() != -1;
                 view.getDeleteBookingButton().setEnabled(rowSelected);
            }
        });
    }

    private void loadMoviesIntoTable() {
        DefaultTableModel model = view.getMovieTableModel();
        model.setRowCount(0);

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

    private void onMovieTableSelect() {
        int selectedRow = view.getMovieTable().getSelectedRow();
        if (selectedRow == -1) {
            onClearForm();
            return;
        }

        DefaultTableModel model = view.getMovieTableModel();
        String id = model.getValueAt(selectedRow, 0).toString();
        String title = model.getValueAt(selectedRow, 1).toString();
        String genre = model.getValueAt(selectedRow, 2).toString();
        String duration = model.getValueAt(selectedRow, 3).toString();
        String posterPath = model.getValueAt(selectedRow, 4) != null ? model.getValueAt(selectedRow, 4).toString() : ""; 

        view.getMovieIdField().setText(id);
        view.getMovieTitleField().setText(title);
        view.getMovieGenreField().setText(genre);
        view.getMovieDurationField().setText(duration);
        view.getMoviePosterField().setText(posterPath);

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

        view.getAddMovieButton().setEnabled(true);
        view.getUpdateMovieButton().setEnabled(false);
        view.getDeleteMovieButton().setEnabled(false);
        view.getMovieTable().clearSelection();
    }

    private void onAddMovie() {
        try {
            String title = view.getMovieTitleField().getText();
            String genre = view.getMovieGenreField().getText();
            int duration = Integer.parseInt(view.getMovieDurationField().getText());
            String posterPath = view.getMoviePosterField().getText();

            if (title.isEmpty() || genre.isEmpty()) {
                showError("Title and Genre cannot be empty.");
                return;
            }
            Movie movie = new Movie(title, genre, duration, posterPath);
            boolean success = movieDAO.addMovie(movie);

            if (success) {
                showMessage("Movie added successfully!");
                loadMoviesIntoTable();
                loadMoviesIntoDropdown();
                onClearForm();
            } else {
                showError("Failed to add movie.");
            }
        } catch (NumberFormatException ex) {
            showError("Duration must be a valid number (e.g., 120).");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Database error: " + ex.getMessage());
        }
    }

    private void onUpdateMovie() {
        try {
            int id = Integer.parseInt(view.getMovieIdField().getText());
            String title = view.getMovieTitleField().getText();
            String genre = view.getMovieGenreField().getText();
            int duration = Integer.parseInt(view.getMovieDurationField().getText());
            String posterPath = view.getMoviePosterField().getText();

            Movie movie = new Movie(id, title, genre, duration, posterPath);
            boolean success = movieDAO.updateMovie(movie);

            if (success) {
                showMessage("Movie updated successfully!");
                loadMoviesIntoTable();
                loadMoviesIntoDropdown();
                onClearForm();
            } else {
                showError("Failed to update movie.");
            }
        } catch (NumberFormatException ex) {
            showError("ID and Duration must be valid numbers.");
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
                    loadShowsIntoTable();
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
                showError("Cannot delete movie. It is currently used by shows or bookings.");
            } else {
                showError("Database error: " + ex.getMessage());
            }
        }
    }

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
                    show.getMovieTitle(),
                    show.getScreenId(),
                    new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(show.getShowTime()),
                    String.format("%.2f", show.getPrice())
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading shows: " + e.getMessage());
        }
    }

    private void onAddShow() {
        try {
            Movie selectedMovie = (Movie) view.getMovieDropdown().getSelectedItem();
            if (selectedMovie == null) {
                showError("Please select a movie.");
                return;
            }
            int movieId = selectedMovie.getMovieId();
            int screenId = Integer.parseInt(view.getScreenField().getText());
            double price = Double.parseDouble(view.getPriceField().getText());

            String timeString = view.getShowTimeField().getText();
            Timestamp showTime = Timestamp.valueOf(timeString + ":00");

            Show show = new Show(movieId, screenId, showTime, price);
            boolean success = showDAO.addShow(show);

            if (success) {
                showMessage("Show added successfully!");
                loadShowsIntoTable();
                view.getScreenField().setText("");
                view.getShowTimeField().setText("");
                view.getPriceField().setText("");
            } else {
                showError("Failed to add show.");
            }

        } catch (NumberFormatException ex) {
            showError("Screen and Price must be valid numbers.");
        } catch (IllegalArgumentException ex) {
             showError("Invalid Timestamp format. Use YYYY-MM-DD HH:MM (24-hour format, e.g., 14:30 for 2:30 PM)");
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
                    view.getDeleteShowButton().setEnabled(false);
                } else {
                    showError("Failed to delete show.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                 if (ex.getMessage().contains("foreign key constraint fails")) {
                    showError("Cannot delete show. It has associated bookings.\nDelete associated bookings first.");
                 } else {
                    showError("Database error: " + ex.getMessage());
                 }
            }
        }
    }

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
                    booking.getMovieTitle(),
                    booking.getFormattedShowTime(),
                    booking.getSelectedSeats(),
                    String.format("%.2f", booking.getTotalAmount()),
                    booking.getFormattedBookingTime()
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
                    loadBookingsIntoTable();
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
}
