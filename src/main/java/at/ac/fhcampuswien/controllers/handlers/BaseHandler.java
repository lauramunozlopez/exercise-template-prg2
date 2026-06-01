package at.ac.fhcampuswien.controllers.handlers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.controllers.HttpAction;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class BaseHandler implements HttpAction {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String response = "{ \"message\": \"Base endpoint in /api/hello/!\" }";
            ApiUtils.sendResponse(exchange, 200, response);
        } else {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
        }
    }
}