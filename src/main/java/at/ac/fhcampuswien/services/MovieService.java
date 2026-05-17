package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Service: enthält die Business Logic und delegiert DB-Operationen ans Repository
public class MovieService {

    private MovieRepository movieRepository;

    // Repository wird per Konstruktor injiziert (gut für Tests + Mockito) yoaa
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() throws DatabaseException {
        return movieRepository.findAll();
    }

    public List<Movie> searchMovies(String title, String genre, Integer releaseYear) throws DatabaseException {
        return movieRepository.findAll().stream()
                .filter(m -> title == null || m.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(m -> genre == null || m.getGenre().equalsIgnoreCase(genre))
                .filter(m -> releaseYear == null || m.getReleaseYear() == releaseYear)
                .collect(Collectors.toList());
    }

    public void addMovie(String title, String genre, int releaseYear) throws DatabaseException {
        Movie movie = new Movie(title, genre, releaseYear);
        movieRepository.add(movie);
    }

    public void deleteMovie(String title, String genre, int releaseYear)
            throws DatabaseException, MovieNotFoundException {
        Movie movie = new Movie(title, genre, releaseYear);
        movieRepository.delete(movie);
    }

    public void updateMovie(String id, String title, String genre, int releaseYear)
            throws DatabaseException, MovieNotFoundException {
        Movie movie = new Movie(title, genre, releaseYear);
        movie.setId(UUID.fromString(id));
        movieRepository.update(movie);
    }
}