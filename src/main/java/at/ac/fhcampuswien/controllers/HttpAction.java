package at.ac.fhcampuswien.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public interface HttpAction {
    void handle(HttpExchange exchange) throws IOException;
}