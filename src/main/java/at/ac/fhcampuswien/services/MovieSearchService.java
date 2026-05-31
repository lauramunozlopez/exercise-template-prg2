/// S principle single responsability

package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;

import java.util.List;
import java.util.stream.Collectors;

public class MovieSearchService {

    public List<Movie> searchMovies(List<Movie> movies,
                                    String title,
                                    String genre,
                                    Integer releaseYear) {

        return movies.stream()
                .filter(m -> title == null || m.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(m -> genre == null || m.getGenre().equalsIgnoreCase(genre))
                .filter(m -> releaseYear == null || m.getReleaseYear() == releaseYear)
                .collect(Collectors.toList());
    }
}
