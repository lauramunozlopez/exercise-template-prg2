package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import at.ac.fhcampuswien.services.MovieSearchService;

class MovieServiceTest {

    private MovieService movieService;
    private MovieRepository movieRepository;

    @BeforeEach
    void set_up() throws DatabaseException {
        // Repository wird gemockt - wir brauchen keine echte Datenbank für Tests
        movieRepository = mock(MovieRepository.class);

        List<Movie> movies = new ArrayList<>(Arrays.asList(
                new Movie("Inception", "Sci-Fi", 2010),
                new Movie("The Dark Knight", "Action", 2008),
                new Movie("Interstellar", "Sci-Fi", 2014)
        ));

        when(movieRepository.findAll()).thenReturn(movies);

        MovieSearchService movieSearchService =
        new MovieSearchService();

movieService =
        new MovieService(
                movieRepository,
                movieSearchService
        );
    }

    @Test
    void givenMoviesInDb_whenGetAllMovies_thenReturnAllMovies() throws DatabaseException {
        List<Movie> result = movieService.getAllMovies();
        assertEquals(3, result.size());
    }

    @Test
    void givenGenreFilter_whenSearchMovies_thenReturnOnlyMatchingMovies() throws DatabaseException {
        List<Movie> result = movieService.searchMovies(null, "Sci-Fi", null);
        assertEquals(2, result.size());
    }

    @Test
    void givenPartialTitle_whenSearchMovies_thenReturnMatchingMovies() throws DatabaseException {
        // "inter" soll "Interstellar" finden (Groß/Kleinschreibung egal)
        List<Movie> result = movieService.searchMovies("inter", null, null);
        assertEquals(1, result.size());
        assertEquals("Interstellar", result.get(0).getTitle());
    }

    @Test
    void givenNewMovie_whenAddMovie_thenRepositoryAddIsCalled() throws DatabaseException {
        doNothing().when(movieRepository).add(any(Movie.class));
        assertDoesNotThrow(() -> movieService.addMovie("Dune", "Sci-Fi", 2021));
        verify(movieRepository, times(1)).add(any(Movie.class));
    }

    // --- Exception Tests ---

    @Test
    void givenDbError_whenDeleteMovie_thenThrowDatabaseException()
            throws DatabaseException, MovieNotFoundException {

        // Simuliert einen Datenbankfehler beim Löschen
        doThrow(new DatabaseException("Datenbankverbindung fehlgeschlagen"))
                .when(movieRepository).delete(any(Movie.class));

        // DatabaseException soll weiterpropagiert werden
        assertThrows(DatabaseException.class, () -> {
            movieService.deleteMovie("Inception", "Sci-Fi", 2010);
        });
    }

    @Test
    void givenNonExistingId_whenUpdateMovie_thenThrowMovieNotFoundException()
            throws DatabaseException, MovieNotFoundException {

        // Simuliert dass der Film in der DB nicht existiert
        doThrow(new MovieNotFoundException("Film nicht gefunden"))
                .when(movieRepository).update(any(Movie.class));

        // MovieNotFoundException soll weiterpropagiert werden
        assertThrows(MovieNotFoundException.class, () -> {
            movieService.updateMovie(
                    "00000000-0000-0000-0000-000000000000",
                    "Ghost Movie", "Horror", 2020
            );
        });
    }
}