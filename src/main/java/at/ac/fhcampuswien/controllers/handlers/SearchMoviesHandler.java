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
import java.util.Map;

public class SearchMoviesHandler implements HttpAction {
    private final MovieService movieService;
    private final Gson gson;

    public SearchMoviesHandler(MovieService movieService, Gson gson) {
        this.movieService = movieService;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
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
            ApiUtils.sendResponse(exchange, 200, gson.toJson(result));
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }
}