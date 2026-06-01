package at.ac.fhcampuswien;

import at.ac.fhcampuswien.controllers.HelloController;
import at.ac.fhcampuswien.controllers.MovieController;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.repositories.IMovieRepository;
import at.ac.fhcampuswien.repositories.SqlMovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class Main {
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {
        // 1. Initialize global infrastructure
        if (!tryInitializeDatabase()) {
            return;
        }

        // 2. Dependency Injection / Composition Root
        Gson gson = new Gson();

        // FIX: Provide a concrete anonymous DataSource wrapper for DatabaseUtil
        // Providing a clean anonymous DataSource wrapper without unused warning flags
        DataSource dataSource = new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                try {
                    return DatabaseUtil.getConnection();
                } catch (DatabaseException e) {
                    throw new SQLException("Could not retrieve connection from DatabaseUtil", e);
                }
            }

            // Notice: removing 'throws SQLException' from your empty/mock overrides silences the IDE warnings perfectly!
            @Override public Connection getConnection(String username, String password) { throw new RuntimeException(new SQLFeatureNotSupportedException()); }
            @Override public PrintWriter getLogWriter() { return null; }
            @Override public void setLogWriter(PrintWriter out) {}
            @Override public void setLoginTimeout(int seconds) {}
            @Override public int getLoginTimeout() { return 0; }
            @Override public Logger getParentLogger() throws SQLFeatureNotSupportedException { throw new SQLFeatureNotSupportedException(); }
            @Override public <T> T unwrap(Class<T> iface) { return null; }
            @Override public boolean isWrapperFor(Class<?> iface) { return false; }
        };

        // Build the decoupled data tier
        IMovieRepository movieRepository = new SqlMovieRepository(dataSource);

        // Build the business service layer
        MovieService movieService = new MovieService(movieRepository);

        // Controllers receive their dependencies from the outside
        HelloController helloController = new HelloController();
        MovieController movieController = new MovieController(movieService, gson);

        // 3. Server Startup and Routing setup
        startHttpServer(helloController, movieController);
    }

    private static boolean tryInitializeDatabase() {
        try {
            DatabaseUtil.initializeDatabase();
            System.out.println("Datenbank erfolgreich initialisiert.");
            return true;
        } catch (DatabaseException e) {
            System.err.println("Datenbankfehler: " + e.getMessage());
            return false;
        }
    }

    private static void startHttpServer(HelloController helloController, MovieController movieController) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);

        server.createContext("/api/hello", helloController);
        server.createContext("/api/movies", movieController);

        server.setExecutor(null);
        server.start();
        System.out.println("Server läuft auf http://localhost:" + SERVER_PORT);
    }
}