package com.fudala.lab02.domain;

import lombok.Data;

import java.util.Map;

@Data
public class ExternalApiResponse {
    private Map<String, String> args;
    private String url;
}