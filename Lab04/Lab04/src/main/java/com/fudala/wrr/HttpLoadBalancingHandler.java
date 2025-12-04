package com.fudala.wrr;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public record HttpLoadBalancingHandler(LoadBalancer loadBalancer) implements HttpHandler {

    public HttpLoadBalancingHandler {
        if (loadBalancer == null) {
            throw new IllegalArgumentException("loadBalancer must not be null");
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }
        BackendServer backendServer = loadBalancer.chooseServer();
        backendServer.incrementServedCount();
        String responseBody =
                "{\"backendId\":\"" + backendServer.getId() + "\",\"servedCount\":" + backendServer.getServedCount() + "}";
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
