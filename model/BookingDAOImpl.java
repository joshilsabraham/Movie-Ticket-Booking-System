package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; // Added
import java.util.HashSet;
import java.util.List; // Added
import java.util.Set;

public class BookingDAOImpl implements BookingDAO {

    private Connection conn;

    public BookingDAOImpl() {
        try {
            this.conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database in DAO", e);
        }
    }

    @Override
    public boolean createBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO Bookings (show_id, customer_name, customer_phone, selected_seats, total_amount) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.getShowId());
            stmt.setString(2, booking.getCustomerName());
            stmt.setString(3, booking.getCustomerPhone());
            stmt.setString(4, booking.getSelectedSeats());
            stmt.setDouble(5, booking.getTotalAmount());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        }
    }

    @Override
    public Set<String> getBookedSeats(int showId) throws SQLException {
        Set<String> bookedSeats = new HashSet<>();
        String sql = "SELECT selected_seats FROM Bookings WHERE show_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] seats = rs.getString("selected_seats").split(",");
                    for (String seat : seats) {
                        bookedSeats.add(seat.trim());
                    }
                }
            }
        }
        return bookedSeats;
    }

    @Override
    public List<Booking> getAllBookingsDetailed() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, m.title, s.show_time " +
                     "FROM Bookings b " +
                     "JOIN Shows s ON b.show_id = s.show_id " +
                     "JOIN Movies m ON s.movie_id = m.movie_id " +
                     "ORDER BY b.booking_time DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("booking_id"),
                    rs.getInt("show_id"),
                    rs.getString("customer_name"),
                    rs.getString("customer_phone"),
                    rs.getString("selected_seats"),
                    rs.getDouble("total_amount"),
                    rs.getTimestamp("booking_time"),
                    rs.getString("title"), // Joined movie title
                    rs.getTimestamp("show_time") // Joined show time
                );
                bookings.add(booking);
            }
        }
        return bookings;
    }

    @Override
    public boolean deleteBooking(int bookingId) throws SQLException {
        String sql = "DELETE FROM Bookings WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        }
    }
}