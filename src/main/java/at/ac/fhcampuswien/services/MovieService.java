/// lab2 2.2
/// 
package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;

import java.util.List;

public class MovieService {

    private List<Movie> movies;

    // ✅ Inyección por constructor
    public MovieService(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public List<Movie> searchMovies(String title, String genre, Integer year) {
        return movies.stream()
                .filter(m -> title == null || m.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(m -> genre == null || m.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .filter(m -> year == null || m.getReleaseYear() == year)
                .toList();
    }

    public boolean addMovie(String title, String genre, int year) {

        boolean exists = movies.stream().anyMatch(m ->
                m.getTitle().equalsIgnoreCase(title) &&
                m.getGenre().equalsIgnoreCase(genre) &&
                m.getReleaseYear() == year
        );

        if (exists) return false;

        movies.add(new Movie(title, genre, year));
        return true;
    }

    public boolean deleteMovie(String title, String genre, int year) {
        return movies.removeIf(m ->
                m.getTitle().equalsIgnoreCase(title) &&
                m.getGenre().equalsIgnoreCase(genre) &&
                m.getReleaseYear() == year
        );
    }

    public boolean updateMovie(String id, String title, String genre, int year) {
        return movies.stream()
                .filter(m -> m.getId().toString().equals(id))
                .findFirst()
                .map(m -> {
                    m.setTitle(title);
                    m.setGenre(genre);
                    m.setReleaseYear(year);
                    return true;
                })
                .orElse(false);
    }
}