package model;

import java.sql.SQLException;
import java.util.List; // Added
import java.util.Set;

public interface BookingDAO {
    boolean createBooking(Booking booking) throws SQLException;
    Set<String> getBookedSeats(int showId) throws SQLException;
    List<Booking> getAllBookingsDetailed() throws SQLException; // Added
    boolean deleteBooking(int bookingId) throws SQLException; // Added
}