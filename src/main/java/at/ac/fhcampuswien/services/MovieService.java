package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;

import java.util.List;
import java.util.UUID;

public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieSearchService movieSearchService;

    public MovieService(MovieRepository movieRepository,
                        MovieSearchService movieSearchService) {

        this.movieRepository = movieRepository;
        this.movieSearchService = movieSearchService;
    }

    public List<Movie> getAllMovies() throws DatabaseException {
        return movieRepository.findAll();
    }

    public List<Movie> searchMovies(String title,
                                    String genre,
                                    Integer releaseYear)
            throws DatabaseException {

        List<Movie> movies = movieRepository.findAll();

        return movieSearchService.searchMovies(
                movies,
                title,
                genre,
                releaseYear
        );
    }

    public void addMovie(String title,
                         String genre,
                         int releaseYear)
            throws DatabaseException {

        Movie movie = new Movie(title, genre, releaseYear);
        movieRepository.add(movie);
    }

    public void deleteMovie(String title,
                            String genre,
                            int releaseYear)
            throws DatabaseException, MovieNotFoundException {

        Movie movie = new Movie(title, genre, releaseYear);
        movieRepository.delete(movie);
    }

    public void updateMovie(String id,
                            String title,
                            String genre,
                            int releaseYear)
            throws DatabaseException, MovieNotFoundException {

        Movie movie = new Movie(title, genre, releaseYear);
        movie.setId(UUID.fromString(id));

        movieRepository.update(movie);
    }
}