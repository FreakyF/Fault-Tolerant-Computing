package com.fudala.lab02.infrastructure.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    @NonNull
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            @NonNull byte[] body,
            @NonNull ClientHttpRequestExecution execution
    ) throws IOException {

        logRequest(request, body);

        ClientHttpResponse response = execution.execute(request, body);

        logResponse(response, request);

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info(
                "Outgoing request: method={} url={} headers={} body={}",
                request.getMethod(),
                request.getURI(),
                request.getHeaders(),
                body != null && body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "<empty>"
        );
    }

    private void logResponse(ClientHttpResponse response, HttpRequest originalRequest) throws IOException {
        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

        log.info(
                "Incoming response: url={} statusCode={} headers={} body={}",
                originalRequest.getURI(),
                response.getStatusCode(),
                response.getHeaders(),
                responseBody
        );
    }
}