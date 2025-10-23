import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class MovieBookingApp extends JFrame {

    private JComboBox<String> movieList;
    private JComboBox<String> timeList;
    private JTextArea movieDetails;
    private JTextField seatCountField;
    private JSpinner dateSpinner;
    private JButton viewButton, bookButton;
    private JLabel messageLabel;

    private ArrayList<String> movies = new ArrayList<>();
    private ArrayList<String> showTimes = new ArrayList<>();

    public MovieBookingApp() {
        setTitle("Movie Ticket Booking System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

    movies.add("Inception");
    movies.add("Avatar");
    movies.add("Interstellar");
    movies.add("The Dark Knight");
    movies.add("The Shawshank Redemption");
    movies.add("The Godfather");
    movies.add("Pulp Fiction");
    movies.add("The Matrix");
    movies.add("Forrest Gump");
    movies.add("Gladiator");
    movies.add("The Lord of the Rings");
    movies.add("Jurassic Park");
    // default show times
    showTimes.add("10:00 AM");
    showTimes.add("12:30 PM");
    showTimes.add("3:00 PM");
    showTimes.add("5:30 PM");
    showTimes.add("8:00 PM");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Movie:"));
        movieList = new JComboBox<>(movies.toArray(new String[0]));
        topPanel.add(movieList);
    topPanel.add(new JLabel("    Select Time:"));
    timeList = new JComboBox<>(showTimes.toArray(new String[0]));
    topPanel.add(timeList);

    viewButton = new JButton("View Details");
    topPanel.add(viewButton);
        add(topPanel, BorderLayout.NORTH);

        movieDetails = new JTextArea(8, 35);
        movieDetails.setEditable(false);
        add(new JScrollPane(movieDetails), BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel(new GridLayout(4, 2, 5, 5));
    bottomPanel.add(new JLabel("Enter No. of Seats:"));
    seatCountField = new JTextField();
    bottomPanel.add(seatCountField);

    bottomPanel.add(new JLabel("Select Date:"));
    dateSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
    dateSpinner.setEditor(dateEditor);
    bottomPanel.add(dateSpinner);

    bookButton = new JButton("Book Ticket");
    bottomPanel.add(bookButton);

    messageLabel = new JLabel("", SwingConstants.CENTER);
    bottomPanel.add(messageLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        viewButton.addActionListener(e -> viewMovieDetails());
        bookButton.addActionListener(e -> bookTicket());

        setVisible(true);
    }

    private void viewMovieDetails() {
        String selectedMovie = (String) movieList.getSelectedItem();
        int index = movieList.getSelectedIndex();
    String time = (String) timeList.getSelectedItem();

    movieDetails.setText("Movie: " + selectedMovie + "\n" +
                 "Show Time: " + time + "\n" +
                 "Description: A great cinematic experience!\n");
    }

    private void bookTicket() {
        try {
            String selectedMovie = (String) movieList.getSelectedItem();
            int seats = Integer.parseInt(seatCountField.getText());
            if (seats <= 0) {
                throw new IllegalArgumentException("Seat count must be greater than zero!");
            }

            // compute total cost (100 rupees per seat)
            int totalCost = seats * 100;
            String selectedTime = (String) timeList.getSelectedItem();
            java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(selectedDate);

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "");
            PreparedStatement ps = con.prepareStatement("INSERT INTO bookings (movie, seats) VALUES (?, ?)");
            ps.setString(1, selectedMovie);
            ps.setInt(2, seats);
            ps.executeUpdate();
            con.close();

            messageLabel.setText(seats + " seat(s) booked for " + selectedMovie + " on " + dateStr + " at " + selectedTime + ". Total: Rs " + totalCost);
            seatCountField.setText("");

        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid number of seats!");
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            messageLabel.setText("Database error!");
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new MovieBookingApp();
    }
}

