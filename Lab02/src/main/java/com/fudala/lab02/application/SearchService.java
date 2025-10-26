package com.fudala.lab02.application;

import com.fudala.lab02.domain.ExternalSearchClient;
import com.fudala.lab02.domain.ExternalApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final ExternalSearchClient externalSearchClient;

    public SearchService(ExternalSearchClient externalSearchClient) {
        this.externalSearchClient = externalSearchClient;
    }

    public void runSampleSearch() {
        String query = "spring boot";
        int limit = 5;

        ExternalApiResponse apiResponse = externalSearchClient.search(query, limit);

        log.info("Final URL resolved by remote service: {}", apiResponse.getUrl());
        log.info("Args echoed by remote: {}", apiResponse.getArgs());
    }
}