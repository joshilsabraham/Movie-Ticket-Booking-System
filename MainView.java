package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import model.Show;

public class MainView extends JFrame {

    private JMenuItem adminMenuItem;
    private JPanel movieGridPanel;
    private JComboBox<Show> showTimesComboBox;
    private Map<String, JToggleButton> seatButtons;
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JLabel totalAmountLabel;
    private JButton bookButton;

    private final int SEAT_ROWS = 6;
    private final int SEAT_COLS = 10;

    public MainView() {
        setTitle("Cinema Ticket Booking System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        createMenuBar();

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(createMoviePanel(), BorderLayout.NORTH);
        contentPanel.add(createSelectionPanel(), BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
        add(createBookingBar(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        adminMenuItem = new JMenuItem("Admin Dashboard");
        optionsMenu.add(adminMenuItem);
        menuBar.add(optionsMenu);
        this.setJMenuBar(menuBar);
    }

    private JPanel createMoviePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " Select a Movie ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)));

        movieGridPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(movieGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setPreferredSize(new Dimension(0, 350)); 
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(createLeftSelectionPanel());
        panel.add(createRightSelectionPanel());
        return panel;
    }

    private JPanel createLeftSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JPanel showtimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        showtimePanel.setBorder(BorderFactory.createTitledBorder(" Select a Showtime "));
        
        showTimesComboBox = new JComboBox<>();
        showTimesComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        showTimesComboBox.setPreferredSize(new Dimension(200, 30));
        showtimePanel.add(showTimesComboBox);
        
        panel.add(showtimePanel, BorderLayout.NORTH);

        JPanel seatGridPanel = new JPanel(new BorderLayout(10, 10));
        seatGridPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " Select Your Seats ",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)));

        JLabel screenLabel = new JLabel("--- SCREEN ---", SwingConstants.CENTER);
        screenLabel.setFont(new Font("Arial", Font.BOLD, 16));
        seatGridPanel.add(screenLabel, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(SEAT_ROWS, SEAT_COLS, 5, 5));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        seatButtons = new HashMap<>();
        
        char rowChar = 'A';
        for (int r = 0; r < SEAT_ROWS; r++) {
            for (int c = 1; c <= SEAT_COLS; c++) {
                String seatName = "" + rowChar + c;
                JToggleButton seatButton = new JToggleButton(seatName);
                seatButton.setFont(new Font("Arial", Font.BOLD, 12));
                seatButton.setMargin(new Insets(2, 2, 2, 2));
                seatButton.setEnabled(false);
                
                seatButtons.put(seatName, seatButton);
                grid.add(seatButton);
            }
            rowChar++;
        }
        seatGridPanel.add(grid, BorderLayout.CENTER);
        
        panel.add(seatGridPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRightSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(" Your Details "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Your Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        customerNameField = new JTextField(15);
        formPanel.add(customerNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        customerPhoneField = new JTextField(15);
        formPanel.add(customerPhoneField, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);

        totalAmountLabel = new JLabel("Total: Rs. 0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalAmountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(totalAmountLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBookingBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        
        bookButton = new JButton("Book Selected Seats");
        bookButton.setFont(new Font("Arial", Font.BOLD, 18));
        bookButton.setEnabled(false);
        bookButton.setPreferredSize(new Dimension(300, 50)); 
        
        panel.add(bookButton);
        return panel;
    }

    public JMenuItem getAdminButton() {
        return adminMenuItem;
    }

    public JPanel getMovieGridPanel() {
        return movieGridPanel;
    }

    public JComboBox<Show> getShowTimesComboBox() {
        return showTimesComboBox;
    }

    public Map<String, JToggleButton> getSeatButtons() {
        return seatButtons;
    }

    public JTextField getCustomerNameField() {
        return customerNameField;
    }

    public JTextField getCustomerPhoneField() {
        return customerPhoneField;
    }

    public JLabel getTotalAmountLabel() {
        return totalAmountLabel;
    }

    public JButton getBookButton() {
        return bookButton;
    }
    
    public void addShowTime(Show show) {
        showTimesComboBox.addItem(show);
    }
}
