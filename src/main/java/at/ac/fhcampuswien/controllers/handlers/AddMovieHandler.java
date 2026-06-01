package at.ac.fhcampuswien.controllers.handlers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.controllers.HttpAction;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AddMovieHandler implements HttpAction {
    private final MovieService movieService;
    private final Gson gson;

    public AddMovieHandler(MovieService movieService, Gson gson) {
        this.movieService = movieService;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Movie movie = gson.fromJson(body, Movie.class);

            if (movie == null || movie.getTitle() == null || movie.getGenre() == null) {
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                return;
            }

            // CHANGED HERE: Pass the clean 'movie' domain object directly
            // instead of extracting separate parameters.
            movieService.addMovie(movie);

            ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");

        } catch (JsonSyntaxException e) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Malformed JSON in request body\" }");
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }
}