package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SqlMovieRepository implements IMovieRepository {

    private final DataSource dataSource;

    public SqlMovieRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(Movie movie) throws DatabaseException {
        String sql = "INSERT INTO movies (id, title, genre, release_year) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, movie.getId());
            stmt.setString(2, movie.getTitle());
            stmt.setString(3, movie.getGenre());
            stmt.setInt(4, movie.getReleaseYear());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Film konnte nicht hinzugefügt werden: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Movie> findAll() throws DatabaseException {
        String sql = "SELECT * FROM movies";
        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("release_year")
                );
                movie.setId(UUID.fromString(rs.getString("id")));
                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Filme konnten nicht geladen werden: " + e.getMessage(), e);
        }
        return movies;
    }

    // --- ADDED THIS METHOD TO RESOLVE THE CLASH ---
    @Override
    public List<Movie> findByCriteria(String title, String genre, Integer releaseYear) throws DatabaseException {
        StringBuilder sql = new StringBuilder("SELECT * FROM movies WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (title != null && !title.trim().isEmpty()) {
            sql.append(" AND LOWER(title) LIKE LOWER(?)");
            parameters.add("%" + title.trim() + "%");
        }
        if (genre != null && !genre.trim().isEmpty()) {
            sql.append(" AND LOWER(genre) = LOWER(?)");
            parameters.add(genre.trim());
        }
        if (releaseYear != null) {
            sql.append(" AND release_year = ?");
            parameters.add(releaseYear);
        }

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Dynamically assign the parameters to the statement execution block
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie(
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getInt("release_year")
                    );
                    movie.setId(UUID.fromString(rs.getString("id")));
                    movies.add(movie);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fehler bei der Kriteriensuche: " + e.getMessage(), e);
        }
        return movies;
    }

    @Override
    public void delete(Movie movie) throws DatabaseException, MovieNotFoundException {
        String sql = "DELETE FROM movies WHERE title = ? AND genre = ? AND release_year = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getReleaseYear());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new MovieNotFoundException("Film nicht gefunden: " + movie.getTitle());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Film konnte nicht gelöscht werden: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Movie movie) throws DatabaseException, MovieNotFoundException {
        String sql = "UPDATE movies SET title = ?, genre = ?, release_year = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getReleaseYear());
            @SuppressWarnings("all") Object id = movie.getId();
            stmt.setObject(4, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new MovieNotFoundException("Film nicht gefunden, ID: " + movie.getId());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Film konnte nicht aktualisiert werden: " + e.getMessage(), e);
        }
    }
}