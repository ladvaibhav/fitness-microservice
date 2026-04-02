package com.fitness.aiservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // This is used to replicate Body, raw seen in Postman
    public String getAnswer(String question) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[]{
                                Map.of("text", question)
                })
                }
        );

        // Constructing post request
        String response = webClient.post() // Method POST
                .uri(geminiApiUrl+geminiApiKey) // URL
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)// attach the JSON body
                .retrieve()// execute the request
                .bodyToMono(String.class)// expect a String back
                .block(); // wait for response (Blocking)

        return response;
    }
}
