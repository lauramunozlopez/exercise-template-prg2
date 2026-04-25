package at.ac.fhcampuswien.models;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Movie {
private UUID id;
private String title;
private String genre;
private int releaseYear;
  
// Default constructor
    public Movie() {
        this.id = UUID.randomUUID();
    }

    // Constructor with parameters
    public Movie(String title, String genre, int releaseYear) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

////get und set id , title, genre, year
     public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }  
////// override to string  to return a string respresentation pf the movie objt
    @Override
    public String toString() {
        return "Movie{id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseYear=" + releaseYear +
                '}';
    }
//// random


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


