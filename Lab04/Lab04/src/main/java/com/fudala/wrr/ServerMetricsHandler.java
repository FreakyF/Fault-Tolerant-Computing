package com.fudala.wrr;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public record ServerMetricsHandler(LoadBalancer loadBalancer) implements HttpHandler {

    public ServerMetricsHandler {
        Objects.requireNonNull(loadBalancer);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }
        List<BackendServer> backends = loadBalancer.getBackends();
        StringBuilder response = new StringBuilder();
        response.append("[");
        for (int i = 0; i < backends.size(); i++) {
            BackendServer backend = backends.get(i);
            response.append("{\"backendId\":\"")
                    .append(backend.getId())
                    .append("\",\"weight\":")
                    .append(backend.getWeight())
                    .append(",\"servedCount\":")
                    .append(backend.getServedCount())
                    .append("}");
            if (i < backends.size() - 1) {
                response.append(",");
            }
        }
        response.append("]");
        byte[] bytes = response.toString().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
