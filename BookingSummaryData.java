package model;

import java.util.List;

public class BookingSummaryData {
    private String movieTitle;
    private String showtime;
    private List<String> seatLabels;
    private double totalPrice;

    public BookingSummaryData(String movieTitle, String showtime, List<String> seatLabels, double totalPrice) {
        this.movieTitle = movieTitle;
        this.showtime = showtime;
        this.seatLabels = seatLabels;
        this.totalPrice = totalPrice;
    }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getShowtime() { return showtime; }
    public void setShowtime(String showtime) { this.showtime = showtime; }

    public List<String> getSeatLabels() { return seatLabels; }
    public void setSeatLabels(List<String> seatLabels) { this.seatLabels = seatLabels; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    @Override
    public String toString() {
        return "BookingSummaryData{" +
                "movieTitle='" + movieTitle + '\'' +
                ", showtime='" + showtime + '\'' +
                ", seats=" + seatLabels +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
