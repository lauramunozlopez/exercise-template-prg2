package at.ac.fhcampuswien;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import java.sql.*;

public class DatabaseUtil {

    private static final String JDBC_URL = "jdbc:h2:~/moviesDb";
    private static final String USER = "user";
    private static final String PASSWORD = "pw";

    // Gibt eine Verbindung zur H2 Datenbank zurück
    public static Connection getConnection() throws DatabaseException {
        try {
            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseException("Verbindung zur Datenbank fehlgeschlagen: " + e.getMessage(), e);
        }
    }

    // Erstellt die Tabelle beim Start der App, falls sie noch nicht existiert
    public static void initializeDatabase() throws DatabaseException {
        String sql = "CREATE TABLE IF NOT EXISTS movies (" +
                "id UUID PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "genre VARCHAR(100) NOT NULL, " +
                "release_year INT NOT NULL)";
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new DatabaseException("Tabelle konnte nicht erstellt werden: " + e.getMessage(), e);
        }
    }
}