package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cinema_db";
    private static final String USER = "user";
    private static final String PASS = "password";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName(JDBC_DRIVER);
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                createInitialTables(connection);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver missing.", e);
        }
        return connection;
    }

    private static void createInitialTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS Users (user_id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, password VARCHAR(100) NOT NULL, is_admin BOOLEAN DEFAULT FALSE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Movies (movie_id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(100) NOT NULL, genre VARCHAR(50), duration INT, poster_path VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS Shows (show_id INT AUTO_INCREMENT PRIMARY KEY, movie_id INT NOT NULL, screen_id INT NOT NULL, show_time TIMESTAMP NOT NULL, price DOUBLE NOT NULL, FOREIGN KEY (movie_id) REFERENCES Movies(movie_id) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Bookings (booking_id INT AUTO_INCREMENT PRIMARY KEY, show_id INT NOT NULL, customer_name VARCHAR(100), customer_phone VARCHAR(50), selected_seats VARCHAR(255) NOT NULL, total_amount DOUBLE NOT NULL, booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (show_id) REFERENCES Shows(show_id) ON DELETE CASCADE)");
        } catch (SQLException e) {
            System.err.println("Error creating initial tables.");
            e.printStackTrace();
        }
    }
}
