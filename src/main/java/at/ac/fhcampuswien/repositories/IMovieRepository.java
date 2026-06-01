package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import java.util.List;

public interface IMovieRepository {
    void add(Movie movie) throws DatabaseException;
    List<Movie> findAll() throws DatabaseException;

    // NEW: Allow the database implementation to perform filtered search queries
    List<Movie> findByCriteria(String title, String genre, Integer releaseYear) throws DatabaseException;

    void delete(Movie movie) throws DatabaseException, MovieNotFoundException;
    void update(Movie movie) throws DatabaseException, MovieNotFoundException;
}