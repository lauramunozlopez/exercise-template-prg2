package at.ac.fhcampuswien.utils;

import at.ac.fhcampuswien.models.Movie;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovieMockFactory {

    public static List<Movie> generateDummyMovies() {
        List<Movie> movies = new ArrayList<>();
        Random random = new Random();

        String[] titles = {
                "The Lost City", "Dark Horizon", "Golden Empire", "Silent Storm",
                "Broken Dreams", "Last Warrior", "Hidden Truth", "Midnight Escape",
                "Eternal Flame", "Fallen Kingdom"
        };

        String[] genres = {
                "Action", "Drama", "Comedy", "Sci-Fi", "Horror", "Adventure"
        };

        for (int i = 0; i < 20; i++) {
            String title = titles[random.nextInt(titles.length)];
            String genre = genres[random.nextInt(genres.length)];
            int year = 1980 + random.nextInt(45);

            movies.add(new Movie(title, genre, year));
        }

        return movies;
    }
}