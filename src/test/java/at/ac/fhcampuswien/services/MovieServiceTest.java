package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.IMovieRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    private MovieService movieService;
    private IMovieRepository movieRepository;

    @BeforeEach
    void set_up() throws DatabaseException {
        movieRepository = mock(IMovieRepository.class);

        List<Movie> allMovies = new ArrayList<>(Arrays.asList(
                new Movie("Inception", "Sci-Fi", 2010),
                new Movie("The Dark Knight", "Action", 2008),
                new Movie("Interstellar", "Sci-Fi", 2014)
        ));
        when(movieRepository.findAll()).thenReturn(allMovies);

        when(movieRepository.findByCriteria(null, "Sci-Fi", null))
                .thenReturn(Arrays.asList(allMovies.get(0), allMovies.get(2)));

        when(movieRepository.findByCriteria("inter", null, null))
                .thenReturn(Arrays.asList(allMovies.get(2)));

        movieService = new MovieService(movieRepository);
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
        List<Movie> result = movieService.searchMovies("inter", null, null);
        assertEquals(1, result.size());
        assertEquals("Interstellar", result.get(0).getTitle());
    }

    @Test
    void givenNewMovie_whenAddMovie_thenRepositoryAddIsCalled() throws DatabaseException {
        doNothing().when(movieRepository).add(any(Movie.class));

        // FIXED: Wrap variables into a single Movie object instead of passing 3 arguments
        Movie targetMovie = new Movie("Dune", "Sci-Fi", 2021);
        assertDoesNotThrow(() -> movieService.addMovie(targetMovie));

        verify(movieRepository, times(1)).add(any(Movie.class));
    }

    // --- Exception Tests ---

    @Test
    void givenDbError_whenDeleteMovie_thenThrowDatabaseException()
            throws DatabaseException, MovieNotFoundException {

        doThrow(new DatabaseException("Datenbankverbindung fehlgeschlagen"))
                .when(movieRepository).delete(any(Movie.class));

        // FIXED: Passing a single Movie object to reflect the new signature
        Movie movieToDelete = new Movie("Inception", "Sci-Fi", 2010);
        assertThrows(DatabaseException.class, () -> {
            movieService.deleteMovie(movieToDelete);
        });
    }

    @Test
    void givenNonExistingId_whenUpdateMovie_thenThrowMovieNotFoundException()
            throws DatabaseException, MovieNotFoundException {

        doThrow(new MovieNotFoundException("Film nicht gefunden"))
                .when(movieRepository).update(any(Movie.class));

        // FIXED: Wrap data into a single Movie object and assign a specific UUID
        // to match the exact structural interface pattern used in your handlers
        Movie movieToUpdate = new Movie("Ghost Movie", "Horror", 2020);
        movieToUpdate.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        assertThrows(MovieNotFoundException.class, () -> {
            movieService.updateMovie(movieToUpdate);
        });
    }
}