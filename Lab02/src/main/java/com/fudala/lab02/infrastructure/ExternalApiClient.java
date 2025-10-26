package com.fudala.lab02.infrastructure;

import com.fudala.lab02.domain.ApiException;
import com.fudala.lab02.domain.ExternalApiResponse;
import com.fudala.lab02.domain.ExternalSearchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ExternalApiClient implements ExternalSearchClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ExternalApiClient(
            RestTemplate restTemplate,
            @Value("${app.external-api.base-url}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public ExternalApiResponse search(String query, int limit) {
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("q", query)
                .queryParam("limit", limit)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ExternalApiResponse> responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    httpEntity,
                    ExternalApiResponse.class
            );

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new ApiException("Non-2xx status from external API: " + responseEntity.getStatusCode());
            }

            ExternalApiResponse body = responseEntity.getBody();
            if (body == null) {
                throw new ApiException("Empty body from external API");
            }

            return body;

        } catch (HttpStatusCodeException e) {
            String msg = "Remote API error. Status: " + e.getStatusCode()
                    + " Body: " + e.getResponseBodyAsString(StandardCharsets.UTF_8);
            throw new ApiException(msg, e);

        } catch (RestClientException e) {
            String msg = "Transport or deserialization error calling external API: " + e.getMessage();
            throw new ApiException(msg, e);
        }
    }
}