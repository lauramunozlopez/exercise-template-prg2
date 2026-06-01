package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.controllers.handlers.*;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MovieController implements HttpHandler {

    private final Map<String, HttpAction> routes = new HashMap<>();

    // Fulfills DIP: Dependencies are passed in from the outside (e.g., from your App initialization main class)
    public MovieController(MovieService movieService, Gson gson) {
        String base = "/api/movies/";

        routes.put(base + "getAll", new GetAllMoviesHandler(movieService, gson));
        routes.put(base + "search", new SearchMoviesHandler(movieService, gson));
        routes.put(base + "add",    new AddMovieHandler(movieService, gson));
        routes.put(base + "delete", new DeleteMovieHandler(movieService, gson));
        routes.put(base + "update", new UpdateMovieHandler(movieService, gson));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        HttpAction action = routes.get(path);

        if (action != null) {
            action.handle(exchange);
        } else {
            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Path not found\" }");
        }
    }
}