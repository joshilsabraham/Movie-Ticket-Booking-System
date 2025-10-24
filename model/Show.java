package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Show {

    private int showId;
    private int movieId;
    private int screenId;
    private Timestamp showTime;
    private double price;
    private String movieTitle;

    public Show(int movieId, int screenId, Timestamp showTime, double price) {
        this.movieId = movieId;
        this.screenId = screenId;
        this.showTime = showTime;
        this.price = price;
    }
    
    public Show(int showId, int movieId, int screenId, Timestamp showTime, double price) {
        this.showId = showId;
        this.movieId = movieId;
        this.screenId = screenId;
        this.showTime = showTime;
        this.price = price;
    }

    public int getShowId() { return showId; }
    public int getMovieId() { return movieId; }
    public int getScreenId() { return screenId; }
    public Timestamp getShowTime() { return showTime; }
    public double getPrice() { return price; }
    public String getMovieTitle() { return movieTitle; }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    @Override
    public String toString() {
        String time = new SimpleDateFormat("hh:mm a").format(showTime);
        return String.format("%s - Rs. %.2f", time, price);
    }
}