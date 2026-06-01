package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.DatabaseUtil;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Repository: nur für direkte Datenbankoperationen zuständig, keine Business Logic
public class MovieRepository {

    // Wir benutzen PreparedStatements um SQL-Injection zu verhindern
    public void add(Movie movie) throws DatabaseException {
        String sql = "INSERT INTO movies (id, title, genre, release_year) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, movie.getId());
            stmt.setString(2, movie.getTitle());
            stmt.setString(3, movie.getGenre());
            stmt.setInt(4, movie.getReleaseYear());
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new DatabaseException("Film konnte nicht hinzugefügt werden: " + e.getMessage(), e);
        }
    }

    public List<Movie> findAll() throws DatabaseException {
        String sql = "SELECT * FROM movies";
        List<Movie> movies = new ArrayList<>();
        try {
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("release_year")
                );
                movie.setId(UUID.fromString(rs.getString("id")));
                movies.add(movie);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new DatabaseException("Filme konnten nicht geladen werden: " + e.getMessage(), e);
        }
        return movies;
    }

    public void delete(Movie movie) throws DatabaseException, MovieNotFoundException{
        String sql = "DELETE FROM movies WHERE title = ? AND genre = ? AND release_year = ?";
        try {
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getReleaseYear());
            int rowsAffected = stmt.executeUpdate();

            // Wenn keine Zeile gelöscht wurde, existiert der Film nicht
            if (rowsAffected == 0) {
                throw new MovieNotFoundException("Film nicht gefunden: " + movie.getTitle());
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new DatabaseException("Film konnte nicht gelöscht werden: " + e.getMessage(), e);
        }
    }

    public void update(Movie movie) throws DatabaseException, MovieNotFoundException {
        String sql = "UPDATE movies SET title = ?, genre = ?, release_year = ? WHERE id = ?";
        try {
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getReleaseYear());
            stmt.setObject(4, movie.getId());
            int rowsAffected = stmt.executeUpdate();

            // Wenn keine Zeile aktualisiert wurde, existiert die ID nicht
            if (rowsAffected == 0) {
                throw new MovieNotFoundException("Film nicht gefunden, ID: " + movie.getId());
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new DatabaseException("Film konnte nicht aktualisiert werden: " + e.getMessage(), e);
        }
    }
}