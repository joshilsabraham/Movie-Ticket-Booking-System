import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MovieBookingApp extends JFrame {

    private JComboBox<String> movieList;
    private JComboBox<String> timeList;
    private JTextArea movieDetails;
    // removed seatCountField (we use selected buttons directly)
    private JSpinner dateSpinner;
    private JToggleButton[][] seatButtons;
    private JButton viewButton, bookButton, refreshButton;
    private JLabel posterLabel;
    private JLabel messageLabel;
    private ArrayList<String> movies = new ArrayList<>();
    private ArrayList<String> showTimes = new ArrayList<>();

    public MovieBookingApp() {
        setTitle("Movie Ticket Booking System");
        setSize(520, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        loadMoviesFromDB();

        showTimes.add("10:00 AM");
        showTimes.add("12:30 PM");
        showTimes.add("3:00 PM");
        showTimes.add("5:30 PM");
        showTimes.add("8:00 PM");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Movie:"));
        movieList = new JComboBox<>(movies.toArray(new String[0]));
        topPanel.add(movieList);
        topPanel.add(new JLabel("    Time:"));
        timeList = new JComboBox<>(showTimes.toArray(new String[0]));
        topPanel.add(timeList);
        viewButton = new JButton("View Details");
        topPanel.add(viewButton);
        refreshButton = new JButton("↻");
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

    movieDetails = new JTextArea(8, 25);
    movieDetails.setEditable(false);

    // Poster label (left side)
    posterLabel = new JLabel();
    posterLabel.setPreferredSize(new Dimension(120, 180));
    posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
    posterLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    // Center panel: poster + details + seat grid
    JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
    centerPanel.add(posterLabel, BorderLayout.WEST);
    centerPanel.add(new JScrollPane(movieDetails), BorderLayout.CENTER);

        // seat grid A-F (6 rows) x 1-9 (9 cols)
        JPanel seatPanel = new JPanel(new GridLayout(6, 9, 4, 4));
        seatButtons = new JToggleButton[6][9];
        char rowStart = 'A';
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 9; c++) {
                String label = (char) (rowStart + r) + String.valueOf(c + 1);
                JToggleButton tb = new JToggleButton(label);
                tb.setMargin(new Insets(2, 2, 2, 2));
                tb.setOpaque(true);
                tb.setBackground(null);
                tb.setForeground(Color.BLACK);
                // change color when selected but keep text readable
                tb.addItemListener(ev -> {
                    if (tb.isSelected()) {
                        tb.setBackground(new Color(30, 90, 160));
                        tb.setForeground(Color.WHITE);
                    } else {
                        tb.setBackground(null);
                        tb.setForeground(Color.BLACK);
                    }
                    updateSeatCountFromSelection();
                });
                seatButtons[r][c] = tb;
                seatPanel.add(tb);
            }
        }
        seatPanel.setBorder(BorderFactory.createTitledBorder("Select Seats"));
        centerPanel.add(seatPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

    // bottom panel: date, book button, message
    JPanel bottomPanel = new JPanel(new BorderLayout());
    JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
    controls.add(new JLabel("Select Date:"));
    dateSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
    dateSpinner.setEditor(dateEditor);
    controls.add(dateSpinner);
    bookButton = new JButton("Book Ticket");
    controls.add(bookButton);
    bottomPanel.add(controls, BorderLayout.NORTH);
    messageLabel = new JLabel("", SwingConstants.CENTER);
    messageLabel.setFont(new Font(messageLabel.getFont().getName(), Font.BOLD, 16));
    bottomPanel.add(messageLabel, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);

        viewButton.addActionListener(e -> viewMovieDetails());
        bookButton.addActionListener(e -> bookTicket());
        refreshButton.addActionListener(e -> refreshMovies());

        setVisible(true);
    }

    private void loadMoviesFromDB() {
        movies.clear();
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT title FROM movies");
            while (rs.next()) {
                movies.add(rs.getString("title"));
            }
            con.close();
        } catch (SQLException e) {
            movies.add("Inception");
            movies.add("Avatar");
            movies.add("Interstellar");
            movies.add("The Dark Knight");
        }
    }

    private void refreshMovies() {
        loadMoviesFromDB();
        movieList.setModel(new DefaultComboBoxModel<>(movies.toArray(new String[0])));
        messageLabel.setText("Movie list refreshed");
    }

    // update seatCountField to reflect number of toggled seats
    private void updateSeatCountFromSelection() {
        int count = 0;
        StringBuilder selected = new StringBuilder();
        for (int r = 0; r < seatButtons.length; r++) {
            for (int c = 0; c < seatButtons[r].length; c++) {
                if (seatButtons[r][c].isSelected()) {
                    count++;
                    if (selected.length() > 0) selected.append(",");
                    selected.append(seatButtons[r][c].getText());
                }
            }
        }
    // seatCountField removed; use selected buttons directly
        // store selected seats list in messageLabel tooltip for quick feedback
    if (selected.length() > 0) {
            messageLabel.setToolTipText(selected.toString());
        } else {
            messageLabel.setToolTipText(null);
        }
        // show total price live (100 rupees per seat)
        int total = count * 100;
        if (count > 0) {
            messageLabel.setText("Selected " + count + " seat(s). Total: Rs " + total);
        } else {
            messageLabel.setText("");
        }
    }

    private void viewMovieDetails() {
        String selectedMovie = (String) movieList.getSelectedItem();
        String time = (String) timeList.getSelectedItem();
        movieDetails.setText("Movie: " + selectedMovie + "\nTime: " + time + "\nDescription: A great cinematic experience!");
        // try to load poster image for the selected movie
        ImageIcon poster = loadPosterForMovie(selectedMovie);
        if (poster != null) posterLabel.setIcon(poster);
        else posterLabel.setIcon(null);
    }

    // load poster from images/<normalized-name>.jpg or .png, scaled to fit posterLabel
    private ImageIcon loadPosterForMovie(String movieName) {
        if (movieName == null) return null;
        try {
            String norm = movieName.toLowerCase().replaceAll("[^a-z0-9]", "_");
            java.nio.file.Path base = java.nio.file.Paths.get("images");
            java.nio.file.Path jpg = base.resolve(norm + ".jpg");
            java.nio.file.Path png = base.resolve(norm + ".png");
            java.awt.Image img = null;
            if (java.nio.file.Files.exists(jpg)) img = new ImageIcon(jpg.toString()).getImage();
            else if (java.nio.file.Files.exists(png)) img = new ImageIcon(png.toString()).getImage();
            if (img == null) return null;
            // scale to posterLabel size keeping aspect
            int w = posterLabel.getPreferredSize().width;
            int h = posterLabel.getPreferredSize().height;
            java.awt.Image scaled = img.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            return null;
        }
    }

    private void bookTicket() {
        try {
            String selectedMovie = (String) movieList.getSelectedItem();
            // count selected seats
            int seats = 0;
            for (int r = 0; r < seatButtons.length; r++) {
                for (int c = 0; c < seatButtons[r].length; c++) {
                    if (seatButtons[r][c].isSelected()) seats++;
                }
            }
            if (seats <= 0) throw new IllegalArgumentException("Select at least one seat");
            String selectedTime = (String) timeList.getSelectedItem();
            Date selectedDate = (Date) dateSpinner.getValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(selectedDate);
            int totalCost = seats * 100;

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "");
            PreparedStatement ps = con.prepareStatement("INSERT INTO bookings (movie, seats, date, time, total) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, selectedMovie);
            ps.setInt(2, seats);
            ps.setString(3, dateStr);
            ps.setString(4, selectedTime);
            ps.setInt(5, totalCost);
            ps.executeUpdate();
            con.close();

            // collect selected seat labels (if any)
            StringBuilder seatsList = new StringBuilder();
            for (int r = 0; r < seatButtons.length; r++) {
                for (int c = 0; c < seatButtons[r].length; c++) {
                    if (seatButtons[r][c].isSelected()) {
                        if (seatsList.length() > 0) seatsList.append(",");
                        seatsList.append(seatButtons[r][c].getText());
                    }
                }
            }
            String seatsInfo = seatsList.length() > 0 ? (" [" + seatsList.toString() + "]") : "";

            messageLabel.setText("Booked " + seats + " seats for " + selectedMovie + " (" + dateStr + " " + selectedTime + ")" + seatsInfo + ". Total: Rs " + totalCost);

        } catch (NumberFormatException e) {
            messageLabel.setText("Enter valid seat number");
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            messageLabel.setText("Database connection error");
        }
    }

    public static void main(String[] args) {
        new MovieBookingApp();
    }
}

