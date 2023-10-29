package org.example.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.resilience4j.ratelimiter.RateLimiter;
import org.example.enums.WeatherApiUrlEnum;
import org.example.exceptions.weatherApi.ResponseException;
import org.example.services.WeatherClientService;
import org.example.validation.WeatherApiValid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class WeatherClientServiceImpl implements WeatherClientService {
    @Qualifier("WebClientApiWeather")
    private final WebClient webClient;

    @Value("${weather.api.authorization-key}")
    private String key;
    private final RateLimiter rateLimiter;

    public WeatherClientServiceImpl(WebClient.Builder webClientBuilder, RateLimiter rateLimiter) {
        this.webClient = webClientBuilder.build();
        this.rateLimiter = rateLimiter;
    }

    @Override
    public JsonNode getCurrentTemperature(String location) {
        return rateLimiter.executeSupplier(() -> {
            String baseUrl = WeatherApiUrlEnum.CURRENT_WEATHER_URL.getMessage();
            String apiUrl = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("q", location)
                    .queryParam("key", key)
                    .build()
                    .toUriString();
            return handleResponse(apiUrl, baseUrl);
        });
    }
    private JsonNode handleResponse(String apiUrl, String baseUrl){
        try {
            JsonNode jsonResponse = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            WeatherApiValid.jsonCheckCurrent(jsonResponse);
            return jsonResponse.get("current").get("temp_c");
        }
        catch (WebClientResponseException e){
            throw new ResponseException(e.getResponseBodyAsString(), baseUrl, e.getRawStatusCode());
        }
    }
}
