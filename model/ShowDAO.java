package model;

import java.sql.SQLException;
import java.util.List;

public interface ShowDAO {
    boolean addShow(Show show) throws SQLException;
    List<Show> getAllShows() throws SQLException;
    boolean deleteShow(int showId) throws SQLException;
    List<Show> getShowsByMovieId(int movieId) throws SQLException;
}