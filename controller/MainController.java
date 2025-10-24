package controller;

import view.MainView;
import view.AdminDashboardView;
import controller.AdminController;
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

        try {
            this.movieDAO = new MovieDAOImpl();
            this.showDAO = new ShowDAOImpl();
            this.bookingDAO = new BookingDAOImpl();

            loadMoviesFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view,
                "Could not connect to database. Check XAMPP.\nError: " + e.getMessage(),
                "Fatal Database Error",
                JOptionPane.ERROR_MESSAGE);
            // Attempt to continue gracefully but features will be limited
            // Clear movie grid in case partial load happened
             if (view.getMovieGridPanel() != null) {
                 view.getMovieGridPanel().removeAll();
                 view.getMovieGridPanel().revalidate();
                 view.getMovieGridPanel().repaint();
             }
        }

        // Add listeners regardless of DB connection success
        addListeners();
        // Ensure combo box is initially disabled if DB failed
        if (movieDAO == null || showDAO == null || bookingDAO == null) {
             view.getShowTimesComboBox().setEnabled(false);
             addPlaceholderToShowtimes("Database Error"); // <-- CORRECT, uses the helper        } else {
             // Add initial placeholder if DB is okay
            addPlaceholderToShowtimes("Select movie first...");
            view.getShowTimesComboBox().setEnabled(false); // Still disabled until movie selected
        }
    }

    private void loadMoviesFromDatabase() throws SQLException {
        JPanel movieGrid = view.getMovieGridPanel();
        movieGrid.removeAll();

        List<Movie> movies = movieDAO.getAllMovies();

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
        enableAllSeats(false);
        selectedShow = null;

        try {
            List<Show> shows = showDAO.getShowsByMovieId(movieId);

            if (shows.isEmpty()) {
                addPlaceholderToShowtimes("No shows available");
                view.getShowTimesComboBox().setEnabled(false);
            } else {
                 addPlaceholderToShowtimes("Select a showtime...");
                for (Show show : shows) {
                    view.addShowTime(show);
                }
                view.getShowTimesComboBox().setEnabled(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            addPlaceholderToShowtimes("Error loading shows");
            view.getShowTimesComboBox().setEnabled(false);
        }
    }

    // Helper to add non-selectable placeholder items to the combo box
    private void addPlaceholderToShowtimes(String text) {
         // Create an anonymous Show subclass just for the placeholder text
         // Use a valid constructor call (e.g., with dummy values)
         Show placeholder = new Show(0, 0, 0, null, 0.0) { // Use the full constructor
             @Override
             public String toString() {
                 return text;
             }
             // Ensure getShowId returns 0 so it's treated as invalid selection
             @Override
             public int getShowId() { return 0; }
         };
         view.getShowTimesComboBox().addItem(placeholder);
    }


    private void onShowTimeSelected() {
        Object selectedItem = view.getShowTimesComboBox().getSelectedItem();

        if (selectedItem != null && (selectedItem instanceof Show) && ((Show)selectedItem).getShowId() != 0) {
            selectedShow = (Show) selectedItem;
            loadBookedSeats(selectedShow.getShowId());
        } else {
            selectedShow = null;
            enableAllSeats(false);
        }
    }

    private void loadBookedSeats(int showId) {
        try {
            Set<String> bookedSeats = bookingDAO.getBookedSeats(showId);
            enableAllSeats(true); // Enable all non-booked first

            for (String seatName : bookedSeats) {
                if (view.getSeatButtons().containsKey(seatName)) {
                    JToggleButton seatButton = view.getSeatButtons().get(seatName);
                    seatButton.setEnabled(false);
                    seatButton.setBackground(Color.RED);
                    seatButton.setSelected(false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            enableAllSeats(false);
            JOptionPane.showMessageDialog(view, "Error loading booked seats.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enableAllSeats(boolean enabled) {
         // Reset visual state for all buttons first
         for (JToggleButton seatButton : view.getSeatButtons().values()) {
              seatButton.setSelected(false);
              // Only reset background if it wasn't already booked (red)
              if (seatButton.getBackground() != Color.RED || enabled) {
                   seatButton.setBackground(enabled ? Color.LIGHT_GRAY : null);
              }
              seatButton.setEnabled(enabled); // Apply final enabled state
         }
         updateLiveTotal();
    }


    private void updateLiveTotal() {
        if (selectedShow == null) {
            view.getTotalAmountLabel().setText("Total: Rs. 0.00");
            view.getBookButton().setEnabled(false);
            return;
        }

        int selectedSeatCount = 0;
        for (JToggleButton button : view.getSeatButtons().values()) {
            if (button.isSelected()) {
                selectedSeatCount++;
            }
        }

        double totalPrice = selectedSeatCount * selectedShow.getPrice();
        view.getTotalAmountLabel().setText(String.format("Total: Rs. %.2f", totalPrice));
        view.getBookButton().setEnabled(selectedSeatCount > 0 && selectedShow != null);
    }

    private void onBookNow() {
        if (selectedShow == null) {
            JOptionPane.showMessageDialog(view, "Please select a movie and showtime first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = view.getCustomerNameField().getText().trim();
        String phone = view.getCustomerPhoneField().getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter your Name and Phone Number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> selectedSeatNames = new ArrayList<>();
        for (Map.Entry<String, JToggleButton> entry : view.getSeatButtons().entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedSeatNames.add(entry.getKey());
            }
        }

        if (selectedSeatNames.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please select at least one seat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String seats = String.join(",", selectedSeatNames);
        double totalAmount = selectedSeatNames.size() * selectedShow.getPrice();

        Booking booking = new Booking(selectedShow.getShowId(), name, phone, seats, totalAmount);

        try {
            boolean success = bookingDAO.createBooking(booking);
            if (success) {
                JOptionPane.showMessageDialog(view,
                    "Booking Successful!\nSeats: " + seats + "\nTotal: Rs. " + String.format("%.2f", totalAmount),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

                loadBookedSeats(selectedShow.getShowId());
                view.getCustomerNameField().setText("");
                view.getCustomerPhoneField().setText("");
                updateLiveTotal();
            } else {
                JOptionPane.showMessageDialog(view, "Booking failed. Seats might have been taken.", "Error", JOptionPane.ERROR_MESSAGE);
                loadBookedSeats(selectedShow.getShowId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Database Error during booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } // This brace closes onBookNow

    private void openAdminDashboard() {
        AdminDashboardView adminView = new AdminDashboardView();
        new AdminController(adminView);
        adminView.setVisible(true);
    } // This brace closes openAdminDashboard

} // This is the final brace for the MainController class
