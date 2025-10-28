package controller;

import view.MainView;
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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainController {

    private MainView view;
    private MovieDAO movieDAO;
    private ShowDAO showDAO;
    private BookingDAO bookingDAO;

    private JPanel selectedMoviePanel = null;
    private Movie selectedMovie = null;
    private Show selectedShow = null;

    public MainController(MainView view) {
        this.view = view;
        setupInitialUIState(); // Set up default UI state immediately

        try {
            this.movieDAO = new MovieDAOImpl();
            this.showDAO = new ShowDAOImpl();
            this.bookingDAO = new BookingDAOImpl();

            loadMoviesFromDatabase(); // Load movies only if DB connection is successful

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not connect to database. Please ensure XAMPP is running and the database is accessible.\n\nError: " + e.getMessage(), "Fatal Database Error");
            addPlaceholderToShowtimes("Database Error"); // Display error in dropdown
            view.getShowTimesComboBox().setEnabled(false); // Disable showtime dropdown on DB error
            return; // Stop further initialization if DB connection fails
        }

        addListeners();

        // After successful DB connection, if no movies are loaded, or no movie is initially selected
        if (view.getShowTimesComboBox().getItemCount() == 0) {
            addPlaceholderToShowtimes("Select movie first...");
            view.getShowTimesComboBox().setEnabled(false);
        }
    }

    /**
     * Sets up the initial state of various UI components, often called at controller initialization
     * or on error conditions.
     */
    private void setupInitialUIState() {
        if (view.getMovieGridPanel() != null) {
            view.getMovieGridPanel().removeAll();
            view.getMovieGridPanel().revalidate();
            view.getMovieGridPanel().repaint();
        }
        if (view.getShowTimesComboBox() != null) {
            view.getShowTimesComboBox().removeAllItems(); // Clear any existing items
            view.getShowTimesComboBox().setEnabled(false); // Disable until a movie is selected
        }
        view.getTotalAmountLabel().setText("Total: Rs. 0.00");
        view.getBookButton().setEnabled(false);
        enableAllSeats(false); // Ensure all seats are disabled and reset
        view.getCustomerNameField().setText("");
        view.getCustomerPhoneField().setText("");
    }

    private void loadMoviesFromDatabase() throws SQLException {
        JPanel movieGrid = view.getMovieGridPanel();
        movieGrid.removeAll();

        List<Movie> movies = movieDAO.getAllMovies();

        if (movies.isEmpty()) {
            JLabel noMoviesLabel = new JLabel("No movies available.", SwingConstants.CENTER);
            noMoviesLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            movieGrid.add(noMoviesLabel);
        } else {
            for (Movie movie : movies) {
                JPanel moviePanel = createMoviePanel(movie.getTitle(), movie.getPosterPath());

                moviePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        onMovieSelected(moviePanel, movie);
                    }
                });
                movieGrid.add(moviePanel);
            }
        }
        movieGrid.revalidate();
        movieGrid.repaint();
    }

    private JPanel createMoviePanel(String title, String imagePath) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = null;
        try {
            icon = new ImageIcon(imagePath);
            if (icon.getIconWidth() == -1) {
                throw new Exception("Image not found: " + imagePath);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load image " + imagePath + ". Using default.");
            icon = new ImageIcon("images/default.jpg"); // Ensure you have default.jpg
            if (icon.getIconWidth() == -1) {
                System.err.println("Warning: Default image images/default.jpg not found!");
            }
        }

        Image img = icon.getImage().getScaledInstance(150, 220, Image.SCALE_SMOOTH);
        JLabel posterLabel = new JLabel(new ImageIcon(img));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        panel.add(posterLabel, BorderLayout.CENTER);
        panel.add(titleLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void addListeners() {
        view.getAdminButton().addActionListener(e -> openAdminDashboard());
        view.getShowTimesComboBox().addActionListener(e -> onShowTimeSelected());
        view.getBookButton().addActionListener(e -> onBookNow());

        for (JToggleButton seatButton : view.getSeatButtons().values()) {
            seatButton.addActionListener(e -> updateLiveTotal());
        }
    }

    private void onMovieSelected(JPanel selectedPanel, Movie movie) {
        if (selectedMoviePanel != null) {
            selectedMoviePanel.setBorder(BorderFactory.createEtchedBorder());
        }

        selectedPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
        selectedMoviePanel = selectedPanel;
        selectedMovie = movie;

        loadRealShowtimesForMovie(movie.getMovieId());
    }

    private void loadRealShowtimesForMovie(int movieId) {
        view.getShowTimesComboBox().removeAllItems();
        enableAllSeats(false); // Reset seats
        selectedShow = null;

        try {
            List<Show> shows = showDAO.getShowsByMovieId(movieId);

            if (shows.isEmpty()) {
                addPlaceholderToShowtimes("No shows available");
                view.getShowTimesComboBox().setEnabled(false);
            } else {
                addPlaceholderToShowtimes("Select a showtime..."); // Placeholder at the top
                for (Show show : shows) {
                    view.addShowTime(show);
                }
                view.getShowTimesComboBox().setEnabled(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading shows: " + e.getMessage());
            addPlaceholderToShowtimes("Error loading shows");
            view.getShowTimesComboBox().setEnabled(false);
        }
    }

    /**
     * Adds a special Show object to the dropdown for displaying messages like "Select movie first..."
     * or "No shows available". These placeholder Shows have a showId of 0.
     */
    private void addPlaceholderToShowtimes(String text) {
        Show placeholder = new Show(0, 0, 0, null, 0.0) {
            @Override
            public String toString() {
                return text;
            }
            @Override
            public int getShowId() { return 0; } // Crucial for identifying placeholders
        };
        if (view.getShowTimesComboBox() != null) {
            view.getShowTimesComboBox().addItem(placeholder);
        }
    }

    private void onShowTimeSelected() {
        Object selectedItem = view.getShowTimesComboBox().getSelectedItem();

        // Check if a valid show (not a placeholder) is selected
        if (selectedItem != null && (selectedItem instanceof Show) && ((Show)selectedItem).getShowId() != 0) {
            selectedShow = (Show) selectedItem;
            loadBookedSeats(selectedShow.getShowId()); // Load seats for this valid show
        } else {
            // Placeholder or no valid show selected
            selectedShow = null;
            enableAllSeats(false); // Disable all seats
        }
    }

    private void loadBookedSeats(int showId) {
        try {
            enableAllSeats(true); // First, enable all seats and reset their state to available

            Set<String> bookedSeats = bookingDAO.getBookedSeats(showId);

            for (String seatName : bookedSeats) {
                if (view.getSeatButtons().containsKey(seatName)) {
                    JToggleButton seatButton = view.getSeatButtons().get(seatName);
                    seatButton.setEnabled(false); // Disable booked seats
                    seatButton.setBackground(Color.RED); // Mark as red
                    seatButton.setSelected(false); // Ensure they are not selected
                }
            }
            updateLiveTotal(); // Recalculate total after loading booked seats
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading booked seats from database: " + e.getMessage());
            enableAllSeats(false); // Disable all seats on error
        }
    }

    /**
     * Enables or disables all seat buttons and resets their visual state.
     * This method is designed to prepare the seat panel. loadBookedSeats
     * will then mark specific seats as booked.
     *
     * @param enabled true to enable seats, false to disable.
     */
    private void enableAllSeats(boolean enabled) {
        for (JToggleButton seatButton : view.getSeatButtons().values()) {
            seatButton.setEnabled(enabled);
            seatButton.setSelected(false); // Deselect any selected seat
            seatButton.setBackground(enabled ? Color.LIGHT_GRAY : null); // Reset to default color
        }
        updateLiveTotal(); // Update total after changing seat states
    }

    private void updateLiveTotal() {
        if (selectedShow == null) {
            view.getTotalAmountLabel().setText("Total: Rs. 0.00");
            view.getBookButton().setEnabled(false);
            return;
        }

        int selectedSeatCount = 0;
        for (JToggleButton button : view.getSeatButtons().values()) {
            // Only count seats that are enabled (i.e., not booked) and selected
            if (button.isEnabled() && button.isSelected()) {
                selectedSeatCount++;
            }
        }

        double totalPrice = selectedSeatCount * selectedShow.getPrice();
        view.getTotalAmountLabel().setText(String.format("Total: Rs. %.2f", totalPrice));
        view.getBookButton().setEnabled(selectedSeatCount > 0 && selectedShow != null);
    }

    private void onBookNow() {
        if (selectedShow == null) {
            showError("Please select a movie and showtime first.");
            return;
        }

        String name = view.getCustomerNameField().getText().trim();
        String phone = view.getCustomerPhoneField().getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            showError("Please enter your Name and Phone Number.");
            return;
        }

        // --- VALIDATION ---
        if (!phone.matches("\\d+")) {
            showError("Please enter a valid phone number (digits only).", "Invalid Input");
            return;
        }
        // --- END VALIDATION ---

        List<String> selectedSeatNames = new ArrayList<>();
        for (Map.Entry<String, JToggleButton> entry : view.getSeatButtons().entrySet()) {
            if (entry.getValue().isSelected() && entry.getValue().isEnabled()) { // Ensure only available selected seats are counted
                selectedSeatNames.add(entry.getKey());
            }
        }

        if (selectedSeatNames.isEmpty()) {
            showError("Please select at least one seat.");
            return;
        }

        String seats = String.join(",", selectedSeatNames);
        double totalAmount = selectedSeatNames.size() * selectedShow.getPrice();

        Booking booking = new Booking(selectedShow.getShowId(), name, phone, seats, totalAmount);

        try {
            boolean success = bookingDAO.createBooking(booking);
            if (success) {
                showMessage("Booking Successful!\nSeats: " + seats + "\nTotal: Rs. " + String.format("%.2f", totalAmount));

                // Reset UI after successful booking
                loadBookedSeats(selectedShow.getShowId()); // Reload seats to mark new bookings
                view.getCustomerNameField().setText("");
                view.getCustomerPhoneField().setText("");
                updateLiveTotal(); // Update total to 0
            } else {
                showError("Booking failed. One or more selected seats might have just been taken.", "Booking Failed");
                loadBookedSeats(selectedShow.getShowId()); // Reload seats to show updated status
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Error during booking: " + e.getMessage(), "Database Error");
        }
    }

    private void openAdminDashboard() {
        try {
            AdminDashboardView adminView = new AdminDashboardView();
            // Ensure the AdminController is initialized to handle adminView's logic
            new AdminController(adminView);
            adminView.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open Admin Dashboard.\nError: " + e.getMessage(), "Admin Dashboard Error");
        }
    }

    // --- UTILITY METHODS FOR MESSAGES ---

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(view, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
