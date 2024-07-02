package com.vlambo3.IASpringBootProject.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlambo3.IASpringBootProject.model.SentimentResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
@Service
public class SentimentService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "https://api-inference.huggingface.co/models/distilbert-base-uncased-finetuned-sst-2-english";
    private final String apiKey = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    public SentimentResult analyzeSentiment(String comment) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "{ \"inputs\": \"" + comment + "\" }";
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        // Realizamos la solicitud a la API de Hugging Face
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        // Imprimimos la respuesta JSON en la consola
        System.out.println("Response JSON: " + response.getBody());

        // Procesamos la respuesta JSON para mapearla a un array de arrays de SentimentResult
        ObjectMapper objectMapper = new ObjectMapper();
        SentimentResult[][] results = null;
        try {
            results = objectMapper.readValue(response.getBody(), SentimentResult[][].class);
        } catch (Exception e) {
            e.printStackTrace(); // Manejo de excepciones, puede ser más específico en un entorno de producción
        }

        // Aseguramos que tenemos resultados y filtramos el resultado más relevante
        if (results != null && results.length > 0) {
            return Arrays.stream(results[0])
                    .max(Comparator.comparing(SentimentResult::getScore))
                    .orElse(null);
        }
        return null;
    }

}
