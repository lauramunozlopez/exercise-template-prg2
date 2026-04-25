package at.ac.fhcampuswien.controllers;

import com.google.gson.Gson; /// lab2

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.models.Movie;
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
    private MovieService movieService = new MovieService(Movie.generateDummyMovies());

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case BASE + "getAll" -> handleGetAll(method, exchange);
            case BASE + "add" -> handleAdd(method, exchange);
            case BASE + "delete" -> handleDelete(method, exchange);
            case BASE + "update" -> handleUpdate(method, exchange);
            case BASE + "search" -> handleSearch(method, exchange); /////lab2
            default -> ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Path not found\" }");
        }
    }

    private void handleGetAll(String method, HttpExchange exchange) throws IOException {

        if (!method.equals("GET")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        List<Movie> movies = movieService.getAllMovies();
        ApiUtils.sendResponse(exchange, 200, buildJson(movies));
    }

    private void handleSearch(String method, HttpExchange exchange) throws IOException { ////lab2

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
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid year\" }");
                return;
            }
        }

        final Integer year = parsedYear;

        List<Movie> filtered = movieService.searchMovies(title, genre, year);

        ApiUtils.sendResponse(exchange, 200, buildJson(filtered));
    }

    private void handleAdd(String method, HttpExchange exchange) throws IOException {

    if (!method.equals("POST")) {
        ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
        return;
    }

    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

    Movie movie = gson.fromJson(body, Movie.class);

    if (movie.getTitle() == null || movie.getGenre() == null) {
        ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
        return;
    }

    boolean added = movieService.addMovie(
            movie.getTitle(),
            movie.getGenre(),
            movie.getReleaseYear()
    );

    if (!added) {
        ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Movie already exists\" }");
        return;
    }

    ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");
}

/////
    private void handleDelete(String method, HttpExchange exchange) throws IOException {

    if (!method.equals("DELETE")) {
        ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
        return;
    }

    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

    Movie movie = gson.fromJson(body, Movie.class);

    boolean deleted = movieService.deleteMovie(
            movie.getTitle(),
            movie.getGenre(),
            movie.getReleaseYear()
    );

    if (!deleted) {
        ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
        return;
    }

    ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");
}

////update

    private void handleUpdate(String method, HttpExchange exchange) throws IOException {

    if (!method.equals("PUT")) {
        ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
        return;
    }

    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

    Movie movie = gson.fromJson(body, Movie.class);

    boolean updated = movieService.updateMovie(
            movie.getId().toString(),
            movie.getTitle(),
            movie.getGenre(),
            movie.getReleaseYear()
    );

    if (!updated) {
        ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
        return;
    }

    ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie updated successfully\" }");
}

    // 🔧 reutilizamos JSON builder
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

   /*   private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);

        if (start == -1) return null;

        start += pattern.length();

        if (json.charAt(start) == '\"') {
            start++;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } else {
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end).trim();
        }
    } delete lab2 punt 3*/ 
}
