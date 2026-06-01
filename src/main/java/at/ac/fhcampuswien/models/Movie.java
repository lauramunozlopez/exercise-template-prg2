package at.ac.fhcampuswien.models;

import java.util.UUID;

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

    // Getters and Setters
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

    @Override
    public String toString() {
        return "Movie{id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseYear=" + releaseYear +
                '}';
    }
}