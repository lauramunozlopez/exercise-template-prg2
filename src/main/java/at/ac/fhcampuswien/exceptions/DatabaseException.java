package at.ac.fhcampuswien.exceptions;

// Checked Exception - wird geworfen bei Datenbankfehlern
public class DatabaseException extends Exception {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}