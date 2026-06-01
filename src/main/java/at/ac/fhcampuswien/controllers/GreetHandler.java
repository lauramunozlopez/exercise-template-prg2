package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GreetHandler implements HttpAction {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            String response = "{ \"message\": \"Hello, friend!\" }";
            ApiUtils.sendResponse(exchange, 200, response);
        } else if ("POST".equals(method)) {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String response = "{ \"received\": " + requestBody + " }";
            ApiUtils.sendResponse(exchange, 200, response);
        } else {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
        }
    }
}