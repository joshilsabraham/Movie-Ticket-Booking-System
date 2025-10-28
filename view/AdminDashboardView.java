package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.Movie;

public class AdminDashboardView extends JFrame {

    private JTabbedPane tabbedPane;

    // Movie components
    private JPanel manageMoviesPanel;
    private JTable movieTable;
    private DefaultTableModel movieTableModel;
    private JTextField movieIdField;
    private JTextField movieTitleField;
    private JTextField movieGenreField;
    private JTextField movieDurationField;
    private JTextField moviePosterField;
    private JButton addMovieButton;
    private JButton updateMovieButton;
    private JButton deleteMovieButton;
    private JButton clearMovieFormButton;

    // Show components
    private JPanel manageShowsPanel;
    private JTable showTable;
    private DefaultTableModel showTableModel;
    private JComboBox<Movie> movieDropdown;
    private JTextField screenField;
    private JTextField showTimeField;
    private JTextField priceField;
    private JButton addShowButton;
    private JButton deleteShowButton;

    // Booking components
    private JPanel viewBookingsPanel;
    private JTable bookingTable;
    private DefaultTableModel bookingTableModel;
    private JButton deleteBookingButton;


    public AdminDashboardView() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        createManageMoviesTab();
        tabbedPane.addTab("Manage Movies", manageMoviesPanel);

        createManageShowsTab();
        tabbedPane.addTab("Manage Shows", manageShowsPanel);

        createViewBookingsTab();
        tabbedPane.addTab("View Bookings", viewBookingsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Helper method to add a label and component pair to a GridBagLayout panel.
     */
    private void addFormField(JPanel panel, String labelText, Component component, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, double weightx) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = 1; // Label width
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = gridx + 1; // Component in next cell
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth; // Component width
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = weightx;
        panel.add(component, gbc);
    }

    private void createManageMoviesTab() {
        manageMoviesPanel = new JPanel(new BorderLayout(10, 10));
        manageMoviesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Table ---
        String[] movieColumnNames = {"ID", "Title", "Genre", "Duration (mins)", "Poster Path"};
        movieTableModel = new DefaultTableModel(movieColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        movieTable = new JTable(movieTableModel);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(movieTable);
        manageMoviesPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Movie Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Use helper method to add fields
        movieTitleField = new JTextField(20);
        addFormField(formPanel, "Title:", movieTitleField, gbc, 0, 0, 1, 1.0);

        movieGenreField = new JTextField(15);
        addFormField(formPanel, "Genre:", movieGenreField, gbc, 2, 0, 1, 1.0);

        movieDurationField = new JTextField(10);
        addFormField(formPanel, "Duration (mins):", movieDurationField, gbc, 0, 1, 1, 1.0);

        moviePosterField = new JTextField(15);
        addFormField(formPanel, "Poster Path:", moviePosterField, gbc, 2, 1, 1, 1.0);

        movieIdField = new JTextField(); // Hidden field, not added visually

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addMovieButton = new JButton("Add Movie");
        updateMovieButton = new JButton("Update Selected");
        deleteMovieButton = new JButton("Delete Selected");
        clearMovieFormButton = new JButton("Clear Form");
        updateMovieButton.setEnabled(false);
        deleteMovieButton.setEnabled(false);
        buttonPanel.add(addMovieButton);
        buttonPanel.add(updateMovieButton);
        buttonPanel.add(deleteMovieButton);
        buttonPanel.add(clearMovieFormButton);

        // Add button panel spanning the form width
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER; // Center buttons within the span
        formPanel.add(buttonPanel, gbc);

        manageMoviesPanel.add(formPanel, BorderLayout.SOUTH);
    }

    private void createManageShowsTab() {
        manageShowsPanel = new JPanel(new BorderLayout(10, 10));
        manageShowsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Table ---
        String[] showColumnNames = {"Show ID", "Movie Title", "Screen", "Showtime", "Price"};
        showTableModel = new DefaultTableModel(showColumnNames, 0) {
             @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        showTable = new JTable(showTableModel);
        showTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(showTable);
        manageShowsPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Show"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Use helper method to add fields
        movieDropdown = new JComboBox<>();
        addFormField(formPanel, "Movie:", movieDropdown, gbc, 0, 0, 3, 1.0); // Spans more columns

        showTimeField = new JTextField(20);
        addFormField(formPanel, "Showtime (YYYY-MM-DD HH:MM):", showTimeField, gbc, 0, 1, 3, 1.0); // Spans more columns

        screenField = new JTextField(5);
        addFormField(formPanel, "Screen:", screenField, gbc, 0, 2, 1, 0.5); // Takes half width

        priceField = new JTextField(10);
        addFormField(formPanel, "Price:", priceField, gbc, 2, 2, 1, 0.5); // Takes other half width

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addShowButton = new JButton("Add Show");
        deleteShowButton = new JButton("Delete Selected Show");
        deleteShowButton.setEnabled(false);
        buttonPanel.add(addShowButton);
        buttonPanel.add(deleteShowButton);

        // Add button panel
        gbc.gridx = 0; gbc.gridy = 3; // Start buttons on the next row
        gbc.gridwidth = 4; // Span all columns
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        manageShowsPanel.add(formPanel, BorderLayout.SOUTH);
    }

    private void createViewBookingsTab() {
        viewBookingsPanel = new JPanel(new BorderLayout(10, 10));
        viewBookingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Table ---
        String[] bookingColumnNames = {"Booking ID", "Customer Name", "Phone", "Movie", "Showtime", "Seats", "Total", "Booked At"};
        bookingTableModel = new DefaultTableModel(bookingColumnNames, 0) {
             @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingTable = new JTable(bookingTableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        viewBookingsPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Button ---
        deleteBookingButton = new JButton("Delete Selected Booking");
        deleteBookingButton.setEnabled(false);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(deleteBookingButton);
        viewBookingsPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // --- PUBLIC GETTERS ---
    // (Unchanged - provide access to components)

    public JTable getMovieTable() { return movieTable; }
    public DefaultTableModel getMovieTableModel() { return movieTableModel; }
    public JTextField getMovieIdField() { return movieIdField; }
    public JTextField getMovieTitleField() { return movieTitleField; }
    public JTextField getMovieGenreField() { return movieGenreField; }
    public JTextField getMovieDurationField() { return movieDurationField; }
    public JTextField getMoviePosterField() { return moviePosterField; }
    public JButton getAddMovieButton() { return addMovieButton; }
    public JButton getUpdateMovieButton() { return updateMovieButton; }
    public JButton getDeleteMovieButton() { return deleteMovieButton; }
    public JButton getClearMovieFormButton() { return clearMovieFormButton; }

    public JTable getShowTable() { return showTable; }
    public DefaultTableModel getShowTableModel() { return showTableModel; }
    public JComboBox<Movie> getMovieDropdown() { return movieDropdown; }
    public JTextField getScreenField() { return screenField; }
    public JTextField getShowTimeField() { return showTimeField; }
    public JTextField getPriceField() { return priceField; }
    public JButton getAddShowButton() { return addShowButton; }
    public JButton getDeleteShowButton() { return deleteShowButton; }

    public JTable getBookingTable() { return bookingTable; }
    public DefaultTableModel getBookingTableModel() { return bookingTableModel; }
    public JButton getDeleteBookingButton() { return deleteBookingButton; }
}
