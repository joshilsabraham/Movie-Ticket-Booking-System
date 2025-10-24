package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.Movie;

public class AdminDashboardView extends JFrame {

    private JTabbedPane tabbedPane;

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

    private JPanel manageShowsPanel;
    private JTable showTable;
    private DefaultTableModel showTableModel;
    private JComboBox<Movie> movieDropdown;
    private JTextField screenField;
    private JTextField showTimeField;
    private JTextField priceField;
    private JButton addShowButton;
    private JButton deleteShowButton;

    private JPanel viewBookingsPanel;
    private JTable bookingTable; // Added
    private DefaultTableModel bookingTableModel; // Added
    private JButton deleteBookingButton; // Added


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

        createViewBookingsTab(); // Added call
        tabbedPane.addTab("View Bookings", viewBookingsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void createManageMoviesTab() {
        manageMoviesPanel = new JPanel(new BorderLayout(10, 10));
        manageMoviesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] movieColumnNames = {"ID", "Title", "Genre", "Duration (mins)", "Poster Path"};
        movieTableModel = new DefaultTableModel(movieColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        movieTable = new JTable(movieTableModel);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(movieTable);
        manageMoviesPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Movie Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        movieTitleField = new JTextField(20);
        formPanel.add(movieTitleField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        movieGenreField = new JTextField(15);
        formPanel.add(movieGenreField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Duration (mins):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        movieDurationField = new JTextField(10);
        formPanel.add(movieDurationField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Poster Path:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        moviePosterField = new JTextField(15);
        formPanel.add(moviePosterField, gbc);

        movieIdField = new JTextField();

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

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, gbc);

        manageMoviesPanel.add(formPanel, BorderLayout.SOUTH);
    }

    private void createManageShowsTab() {
        manageShowsPanel = new JPanel(new BorderLayout(10, 10));
        manageShowsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] showColumnNames = {"Show ID", "Movie Title", "Screen", "Showtime", "Price"};
        showTableModel = new DefaultTableModel(showColumnNames, 0) {
             @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        showTable = new JTable(showTableModel);
        showTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(showTable);
        manageShowsPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Show"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Movie:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        movieDropdown = new JComboBox<>();
        formPanel.add(movieDropdown, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Showtime (YYYY-MM-DD HH:MM):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        showTimeField = new JTextField(20);
        formPanel.add(showTimeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Screen:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        screenField = new JTextField(5);
        formPanel.add(screenField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        priceField = new JTextField(10);
        formPanel.add(priceField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addShowButton = new JButton("Add Show");
        deleteShowButton = new JButton("Delete Selected Show");
        deleteShowButton.setEnabled(false);
        buttonPanel.add(addShowButton);
        buttonPanel.add(deleteShowButton);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, gbc);

        manageShowsPanel.add(formPanel, BorderLayout.SOUTH);
    }

    private void createViewBookingsTab() {
        viewBookingsPanel = new JPanel(new BorderLayout(10, 10));
        viewBookingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] bookingColumnNames = {"Booking ID", "Customer Name", "Phone", "Movie", "Showtime", "Seats", "Total", "Booked At"};
        bookingTableModel = new DefaultTableModel(bookingColumnNames, 0) {
             @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingTable = new JTable(bookingTableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        viewBookingsPanel.add(scrollPane, BorderLayout.CENTER);

        deleteBookingButton = new JButton("Delete Selected Booking");
        deleteBookingButton.setEnabled(false);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(deleteBookingButton);

        viewBookingsPanel.add(buttonPanel, BorderLayout.SOUTH);
    }


    public JTable getMovieTable() {
        return movieTable;
    }
    public DefaultTableModel getMovieTableModel() {
        return movieTableModel;
    }
    public JTextField getMovieIdField() {
        return movieIdField;
    }
    public JTextField getMovieTitleField() {
        return movieTitleField;
    }
    public JTextField getMovieGenreField() {
        return movieGenreField;
    }
    public JTextField getMovieDurationField() {
        return movieDurationField;
    }
    public JTextField getMoviePosterField() {
        return moviePosterField;
    }
    public JButton getAddMovieButton() {
        return addMovieButton;
    }
    public JButton getUpdateMovieButton() {
        return updateMovieButton;
    }
    public JButton getDeleteMovieButton() {
        return deleteMovieButton;
    }
    public JButton getClearMovieFormButton() {
        return clearMovieFormButton;
    }

    public JTable getShowTable() {
        return showTable;
    }
    public DefaultTableModel getShowTableModel() {
        return showTableModel;
    }
    public JComboBox<Movie> getMovieDropdown() {
        return movieDropdown;
    }
    public JTextField getScreenField() {
        return screenField;
    }
    public JTextField getShowTimeField() {
        return showTimeField;
    }
    public JTextField getPriceField() {
        return priceField;
    }
    public JButton getAddShowButton() {
        return addShowButton;
    }
    public JButton getDeleteShowButton() {
        return deleteShowButton;
    }

    public JTable getBookingTable() { // Added
        return bookingTable;
    }
    public DefaultTableModel getBookingTableModel() { // Added
        return bookingTableModel;
    }
    public JButton getDeleteBookingButton() { // Added
        return deleteBookingButton;
    }
}
