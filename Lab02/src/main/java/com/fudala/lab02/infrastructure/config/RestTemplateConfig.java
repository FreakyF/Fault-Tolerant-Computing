package com.fudala.lab02.infrastructure.config;

import com.fudala.lab02.infrastructure.http.LoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(LoggingInterceptor loggingInterceptor) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());

        RestTemplate restTemplate = new RestTemplate(
                new BufferingClientHttpRequestFactory(requestFactory)
        );

        restTemplate.getInterceptors().add(loggingInterceptor);

        return restTemplate;
    }
}