import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * CINEMA TICKET BOOKING SYSTEM (Single File Version)
 * Uses: Swing + AWT + JDBC + MySQL
 */
public class CinemaTicketBookingSystem {

    // ---------- DATABASE CONNECTION ----------
    static class DBConnection {
        private static final String URL = "jdbc:mysql://localhost:3306/cinema_db";
        private static final String USER = "root";
        private static final String PASSWORD = ""; // change if needed

        public static Connection getConnection() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database Connected Successfully!");
                return conn;
            } catch (Exception e) {
                System.out.println("❌ Database Connection Failed!");
                e.printStackTrace();
                return null;
            }
        }
    }

    // ---------- LOGIN FRAME ----------
    static class LoginFrame extends JFrame {
        private JTextField emailField;
        private JPasswordField passwordField;

        public LoginFrame() {
            setTitle("Cinema Ticket Booking - Login");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JLabel title = new JLabel("🎬 Cinema Ticket Booking System", SwingConstants.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 16));
            add(title, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

            formPanel.add(new JLabel("Email:"));
            emailField = new JTextField();
            formPanel.add(emailField);

            formPanel.add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            formPanel.add(passwordField);

            JButton loginBtn = new JButton("Login");
            JButton registerBtn = new JButton("Register");

            formPanel.add(loginBtn);
            formPanel.add(registerBtn);

            add(formPanel, BorderLayout.CENTER);

            loginBtn.addActionListener(e -> loginUser());
            registerBtn.addActionListener(e -> {
                dispose();
                new RegisterFrame().setVisible(true);
            });
        }

        private void loginUser() {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) return;

                String sql = "SELECT * FROM users WHERE email=? AND password=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, email);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String username = rs.getString("username");
                    int userId = rs.getInt("id");
                    JOptionPane.showMessageDialog(this, "✅ Login Successful! Welcome " + username);
                    dispose();
                    new MainDashboard(userId, username).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Invalid Email or Password");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // ---------- REGISTER FRAME ----------
    static class RegisterFrame extends JFrame {
        private JTextField usernameField, emailField, phoneField;
        private JPasswordField passwordField;

        public RegisterFrame() {
            setTitle("User Registration");
            setSize(400, 350);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JLabel title = new JLabel("🧾 Register New User", SwingConstants.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 16));
            add(title, BorderLayout.NORTH);

            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

            panel.add(new JLabel("Username:"));
            usernameField = new JTextField();
            panel.add(usernameField);

            panel.add(new JLabel("Email:"));
            emailField = new JTextField();
            panel.add(emailField);

            panel.add(new JLabel("Phone:"));
            phoneField = new JTextField();
            panel.add(phoneField);

            panel.add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            panel.add(passwordField);

            JButton registerBtn = new JButton("Register");
            JButton backBtn = new JButton("Back to Login");
            panel.add(registerBtn);
            panel.add(backBtn);

            add(panel, BorderLayout.CENTER);

            registerBtn.addActionListener(e -> registerUser());
            backBtn.addActionListener(e -> {
                dispose();
                new LoginFrame().setVisible(true);
            });
        }

        private void registerUser() {
            String username = usernameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please fill all required fields!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) return;

                String sql = "INSERT INTO users(username, email, password, phone) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, username);
                pst.setString(2, email);
                pst.setString(3, password);
                pst.setString(4, phone);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Registration Successful!");
                dispose();
                new LoginFrame().setVisible(true);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        }
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
