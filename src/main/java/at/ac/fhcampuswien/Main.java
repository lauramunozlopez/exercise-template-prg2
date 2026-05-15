package at.ac.fhcampuswien;

import at.ac.fhcampuswien.controllers.HelloController;
import at.ac.fhcampuswien.controllers.MovieController;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {

        // Datenbank initialisieren bevor der Server startet
        try {
            DatabaseUtil.initializeDatabase();
            System.out.println("Datenbank erfolgreich initialisiert.");
        } catch (DatabaseException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
            return;
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        server.createContext("/api/hello", new HelloController());
        server.createContext("/api/movies", new MovieController());
        server.setExecutor(null);
        server.start();
        System.out.println("Server läuft auf http://localhost:" + SERVER_PORT);
    }
}