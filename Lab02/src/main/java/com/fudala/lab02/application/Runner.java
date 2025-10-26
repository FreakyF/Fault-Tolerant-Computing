package com.fudala.lab02.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    private final SearchService searchService;

    public Runner(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public void run(String... args) {
        try {
            searchService.runSampleSearch();
        } catch (Exception e) {
            log.error("Startup sample call failed: {}", e.getMessage(), e);
        }
    }
}