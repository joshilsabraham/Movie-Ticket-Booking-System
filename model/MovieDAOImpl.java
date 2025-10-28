package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MovieDAOImpl implements MovieDAO {

    private Connection conn;

    public MovieDAOImpl() {
        try {
            this.conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database in DAO", e);
        }
    }

    @Override
    public boolean addMovie(Movie movie) throws SQLException {
        String sql = "INSERT INTO Movies (title, genre, duration, poster_path) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getDuration());
            stmt.setString(4, movie.getPosterPath());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        }
    }

    @Override
    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movies";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movies.add(extractMovieFromResultSet(rs));
            }
        }
        return movies;
    }

    @Override
    public boolean updateMovie(Movie movie) throws SQLException {
        String sql = "UPDATE Movies SET title = ?, genre = ?, duration = ?, poster_path = ? WHERE movie_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getDuration());
            stmt.setString(4, movie.getPosterPath());
            stmt.setInt(5, movie.getMovieId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        }
    }

    @Override
    public boolean deleteMovie(int movieId) throws SQLException {
        String sql = "DELETE FROM Movies WHERE movie_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        }
    }

    private Movie extractMovieFromResultSet(ResultSet rs) throws SQLException {
        return new Movie(
            rs.getInt("movie_id"),
            rs.getString("title"),
            rs.getString("genre"),
            rs.getInt("duration"),
            rs.getString("poster_path")
        );
    }
}
