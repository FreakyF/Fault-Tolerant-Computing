package com.fudala.lab02.domain;

public interface ExternalSearchClient {
    ExternalApiResponse search(String query, int limit);
}