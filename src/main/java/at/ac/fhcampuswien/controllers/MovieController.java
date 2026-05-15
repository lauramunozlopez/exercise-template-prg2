package at.ac.fhcampuswien.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MovieController implements HttpHandler {

    private Gson gson = new Gson();
    private final String BASE = "/api/movies/";

    // MovieService bekommt jetzt ein MovieRepository statt einer Liste
    private MovieService movieService = new MovieService(new MovieRepository());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case BASE + "getAll" -> handleGetAll(method, exchange);
            case BASE + "add"    -> handleAdd(method, exchange);
            case BASE + "delete" -> handleDelete(method, exchange);
            case BASE + "update" -> handleUpdate(method, exchange);
            case BASE + "search" -> handleSearch(method, exchange);
            default -> ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Path not found\" }");
        }
    }

    private void handleGetAll(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("GET")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }
        try {
            List<Movie> movies = movieService.getAllMovies();
            ApiUtils.sendResponse(exchange, 200, buildJson(movies));
        } catch (DatabaseException e) {
            // DatabaseException -> 500 Internal Server Error
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }

    private void handleSearch(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("GET")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = ApiUtils.parseQueryParams(query);

        String title = params.get("title");
        String genre = params.get("genre");
        String yearStr = params.get("releaseYear");
        Integer parsedYear = null;

        if (yearStr != null) {
            try {
                parsedYear = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid year format\" }");
                return;
            }
        }

        try {
            List<Movie> result = movieService.searchMovies(title, genre, parsedYear);
            ApiUtils.sendResponse(exchange, 200, buildJson(result));
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }

    private void handleAdd(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("POST")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Movie movie = gson.fromJson(body, Movie.class);

            if (movie == null || movie.getTitle() == null || movie.getGenre() == null) {
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                return;
            }

            movieService.addMovie(movie.getTitle(), movie.getGenre(), movie.getReleaseYear());
            ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");

        } catch (JsonSyntaxException e) {
            // JsonSyntaxException (falsches JSON Format) -> 400 Bad Request
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Malformed JSON in request body\" }");
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }

    private void handleDelete(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("DELETE")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Movie movie = gson.fromJson(body, Movie.class);

            if (movie == null || movie.getTitle() == null) {
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                return;
            }

            movieService.deleteMovie(movie.getTitle(), movie.getGenre(), movie.getReleaseYear());
            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");

        } catch (JsonSyntaxException e) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Malformed JSON in request body\" }");
        } catch (MovieNotFoundException e) {
            // MovieNotFoundException -> 404 Not Found
            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"" + e.getMessage() + "\" }");
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }

    private void handleUpdate(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("PUT")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            Movie movie = gson.fromJson(body, Movie.class);

            if (movie == null || movie.getId() == null || movie.getTitle() == null) {
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                return;
            }

            movieService.updateMovie(
                    movie.getId().toString(),
                    movie.getTitle(),
                    movie.getGenre(),
                    movie.getReleaseYear()
            );
            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie updated successfully\" }");

        } catch (JsonSyntaxException e) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Malformed JSON in request body\" }");
        } catch (MovieNotFoundException e) {
            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"" + e.getMessage() + "\" }");
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }

    private String buildJson(List<Movie> movies) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            json.append("{")
                    .append("\"id\":\"").append(m.getId()).append("\",")
                    .append("\"title\":\"").append(m.getTitle()).append("\",")
                    .append("\"genre\":\"").append(m.getGenre()).append("\",")
                    .append("\"releaseYear\":").append(m.getReleaseYear())
                    .append("}");
            if (i < movies.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }
}