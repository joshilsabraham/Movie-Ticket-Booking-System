package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShowDAOImpl implements ShowDAO {

    private Connection conn;

    public ShowDAOImpl() {
        try {
            this.conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database in DAO", e);
        }
    }

    @Override
    public boolean addShow(Show show) throws SQLException {
        String sql = "INSERT INTO Shows (movie_id, screen_id, show_time, price) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, show.getMovieId());
            stmt.setInt(2, show.getScreenId());
            stmt.setTimestamp(3, show.getShowTime());
            stmt.setDouble(4, show.getPrice());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        }
    }

    @Override
    public List<Show> getAllShows() throws SQLException {
        List<Show> shows = new ArrayList<>();
        String sql = "SELECT s.*, m.title " +
                     "FROM Shows s " +
                     "JOIN Movies m ON s.movie_id = m.movie_id " +
                     "ORDER BY s.show_time ASC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Show show = extractShowFromResultSet(rs);
                show.setMovieTitle(rs.getString("title")); 
                shows.add(show);
            }
        }
        return shows;
    }
    
    @Override
    public List<Show> getShowsByMovieId(int movieId) throws SQLException {
        List<Show> shows = new ArrayList<>();
        String sql = "SELECT * FROM Shows WHERE movie_id = ? ORDER BY show_time ASC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    shows.add(extractShowFromResultSet(rs));
                }
            }
        }
        return shows;
    }

    @Override
    public boolean deleteShow(int showId) throws SQLException {
        String sql = "DELETE FROM Shows WHERE show_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        }
    }
    
    private Show extractShowFromResultSet(ResultSet rs) throws SQLException {
        return new Show(
            rs.getInt("show_id"),
            rs.getInt("movie_id"),
            rs.getInt("screen_id"),
            rs.getTimestamp("show_time"),
            rs.getDouble("price")
        );
    }
}