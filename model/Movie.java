package model;

public class Movie {

    private int movieId;
    private String title;
    private String genre;
    private int duration;
    private String posterPath;

    public Movie(int movieId, String title, String genre, int duration, String posterPath) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.posterPath = posterPath;
    }

    public Movie(String title, String genre, int duration, String posterPath) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.posterPath = posterPath;
    }
    
    public int getMovieId() { return movieId; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getDuration() { return duration; }
    public String getPosterPath() { return posterPath; }

    @Override
    public String toString() {
        return this.title;
    }
}