package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Booking {
    private int bookingId; // Added ID
    private int showId;
    private String customerName;
    private String customerPhone;
    private String selectedSeats;
    private double totalAmount;
    private Timestamp bookingTime; // Added booking time

    private String movieTitle;
    private Timestamp showTime;

    public Booking(int showId, String customerName, String customerPhone, String selectedSeats, double totalAmount) {
        this.showId = showId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.selectedSeats = selectedSeats;
        this.totalAmount = totalAmount;
    }

    public Booking(int bookingId, int showId, String customerName, String customerPhone, String selectedSeats, double totalAmount, Timestamp bookingTime, String movieTitle, Timestamp showTimeValue) {
        this.bookingId = bookingId;
        this.showId = showId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.selectedSeats = selectedSeats;
        this.totalAmount = totalAmount;
        this.bookingTime = bookingTime;
        this.movieTitle = movieTitle;
        this.showTime = showTimeValue;
    }


    public int getBookingId() { return bookingId; }
    public int getShowId() { return showId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getSelectedSeats() { return selectedSeats; }
    public double getTotalAmount() { return totalAmount; }
    public Timestamp getBookingTime() { return bookingTime; }
    public String getMovieTitle() { return movieTitle; }
    public Timestamp getShowTime() { return showTime; }

    public String getFormattedShowTime() {
        if (showTime == null) return "N/A";
        return new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(showTime);
    }
     public String getFormattedBookingTime() {
        if (bookingTime == null) return "N/A";
        return new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(bookingTime);
    }
}
