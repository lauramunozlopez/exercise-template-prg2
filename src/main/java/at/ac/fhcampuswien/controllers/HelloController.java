package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.controllers.handlers.BaseHandler;
import at.ac.fhcampuswien.controllers.handlers.InfoHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelloController implements HttpHandler {
    private static final String BASE = "/api/hello/";
    private final Map<String, HttpAction> routes = new HashMap<>();

    public HelloController() {
        // Routes are registered here.
        // To add a new endpoint tomorrow, you just add one line here, or inject this map!
        routes.put(BASE, new BaseHandler());
        routes.put(BASE + "greet", new GreetHandler());
        routes.put(BASE + "info", new InfoHandler());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // Lookup the strategy based on the path
        HttpAction action = routes.get(path);

        if (action != null) {
            action.handle(exchange);
        } else {
            String response = "{ \"error\": \"Path not found\" }";
            ApiUtils.sendResponse(exchange, 404, response);
        }
    }
}