package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
////new
class MovieServiceTest {

    private MovieService movieService;
    private List<Movie> testMovies;

    @BeforeEach
    void setUp() {
        // We use a new ArrayList so the list is mutable (allows add/remove)
        testMovies = new ArrayList<>();
        testMovies.add(new Movie("Inception", "Sci-Fi", 2010));
        testMovies.add(new Movie("Interstellar", "Sci-Fi", 2014));
        testMovies.add(new Movie("The Dark Knight", "Action", 2008));

        // Initialize the service with our fresh test data before every single test
        movieService = new MovieService(testMovies);
    }

    // --- SEARCH MOVIES TESTS ---

    @Test
    void givenExistingTitle_whenSearchMovies_thenReturnMatchedMovie() {
        List<Movie> result = movieService.searchMovies("Inception", null, null);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenPartialTitleAndDifferentCase_whenSearchMovies_thenReturnMatchedMovies() {
        // "inter" should match "Interstellar"
        List<Movie> result = movieService.searchMovies("inter", null, null);

        assertEquals(1, result.size());
        assertEquals("Interstellar", result.get(0).getTitle());
    }

    @Test
    void givenNullParameters_whenSearchMovies_thenReturnAllMovies() {
        List<Movie> result = movieService.searchMovies(null, null, null);

        assertEquals(3, result.size());
    }

    // --- ADD MOVIE TESTS ---

    @Test
    void givenNewMovie_whenAddMovie_thenReturnTrueAndIncreaseListSize() {
        boolean isAdded = movieService.addMovie("Dune", "Sci-Fi", 2021);

        assertTrue(isAdded);
        assertEquals(4, movieService.getAllMovies().size());
    }

    @Test
    void givenExistingMovie_whenAddMovie_thenReturnFalseAndDoNotAdd() {
        // Trying to add a movie that is already in the setUp() list
        boolean isAdded = movieService.addMovie("Inception", "Sci-Fi", 2010);

        assertFalse(isAdded);
        assertEquals(3, movieService.getAllMovies().size()); // Size should not change
    }

    // --- DELETE MOVIE TESTS ---

    @Test
    void givenExistingMovie_whenDeleteMovie_thenReturnTrueAndDecreaseListSize() {
        boolean isDeleted = movieService.deleteMovie("Inception", "Sci-Fi", 2010);

        assertTrue(isDeleted);
        assertEquals(2, movieService.getAllMovies().size());
    }

    @Test
    void givenNonExistingMovie_whenDeleteMovie_thenReturnFalse() {
        boolean isDeleted = movieService.deleteMovie("NonExistent", "Comedy", 1999);

        assertFalse(isDeleted);
        assertEquals(3, movieService.getAllMovies().size());
    }

    // --- UPDATE MOVIE TESTS ---

    @Test
    void givenExistingId_whenUpdateMovie_thenReturnTrueAndApplyChanges() {
        // First, get an ID from an existing movie
        Movie movieToUpdate = movieService.getAllMovies().get(0);
        String id = movieToUpdate.getId().toString();

        // Update it
        boolean isUpdated = movieService.updateMovie(id, "Inception - Director's Cut", "Thriller", 2011);

        assertTrue(isUpdated);
        assertEquals("Inception - Director's Cut", movieToUpdate.getTitle());
        assertEquals("Thriller", movieToUpdate.getGenre());
        assertEquals(2011, movieToUpdate.getReleaseYear());
    }

    @Test
    void givenNonExistingId_whenUpdateMovie_thenReturnFalse() {
        boolean isUpdated = movieService.updateMovie("invalid-id-123", "New Title", "Drama", 2024);

        assertFalse(isUpdated);
    }
}