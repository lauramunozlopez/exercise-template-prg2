package at.ac.fhcampuswien.exceptions;

// Checked Exception - wird geworfen wenn ein Film nicht gefunden wird
public class MovieNotFoundException extends Exception {
    public MovieNotFoundException(String message) {
        super(message);
    }
}