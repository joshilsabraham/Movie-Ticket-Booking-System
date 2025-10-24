package model;

import java.sql.SQLException;
import java.util.List;

public interface MovieDAO {
    boolean addMovie(Movie movie) throws SQLException;
    List<Movie> getAllMovies() throws SQLException;
    boolean updateMovie(Movie movie) throws SQLException;
    boolean deleteMovie(int movieId) throws SQLException;
}