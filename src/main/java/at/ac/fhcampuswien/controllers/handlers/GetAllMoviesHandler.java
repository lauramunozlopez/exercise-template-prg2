package at.ac.fhcampuswien.controllers.handlers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.controllers.HttpAction;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class GetAllMoviesHandler implements HttpAction {
    private final MovieService movieService;
    private final Gson gson;

    public GetAllMoviesHandler(MovieService movieService, Gson gson) {
        this.movieService = movieService;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }
        try {
            List<Movie> movies = movieService.getAllMovies();
            ApiUtils.sendResponse(exchange, 200, gson.toJson(movies));
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }
}