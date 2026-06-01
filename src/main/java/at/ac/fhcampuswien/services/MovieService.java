package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.IMovieRepository;

import java.util.List;

public class MovieService {

    private final IMovieRepository movieRepository;

    // Fulfills DIP: Only depends on the Interface contract
    public MovieService(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // Single Responsibility: Fetching listings
    public List<Movie> getAllMovies() throws DatabaseException {
        return movieRepository.findAll();
    }

    // Single Responsibility: Query delegation
    public List<Movie> searchMovies(String title, String genre, Integer releaseYear)
            throws DatabaseException {
        return movieRepository.findByCriteria(title, genre, releaseYear);
    }

    // FIXED (SRP/OCP): The service no longer cares how a Movie object is manufactured
    public void addMovie(Movie movie) throws DatabaseException {
        movieRepository.add(movie);
    }

    // FIXED (SRP/OCP): No hidden factory work or tight coupling to constructors
    public void deleteMovie(Movie movie) throws DatabaseException, MovieNotFoundException {
        movieRepository.delete(movie);
    }

    // FIXED (SRP/OCP): Updates are clean and clear
    public void updateMovie(Movie movie) throws DatabaseException, MovieNotFoundException {
        movieRepository.update(movie);
    }
}