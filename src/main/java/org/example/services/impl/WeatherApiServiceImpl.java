package org.example.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.example.dto.ResponseErrorDto;
import org.example.enums.WeatherApiUrlEnum;
import org.example.services.WeatherApiService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;


@Service
public class WeatherApiServiceImpl implements WeatherApiService {
    private final WebClient webClient;
    private final String key;
    private final RateLimiter rateLimiter;

    public WeatherApiServiceImpl(WebClient.Builder webClientBuilder, Environment environment, RateLimiter rateLimiter) {
        this.webClient = webClientBuilder.build();
        this.key = environment.getProperty("weather.api.authorization-key");
        this.rateLimiter = rateLimiter;
    }

    @Override
    public ResponseEntity<Object> getCurrentTemperature(String q) {
        try {
            return rateLimiter.executeSupplier(() -> {
                String baseUrl = WeatherApiUrlEnum.CURRENT_WEATHER_URL.getMessage();
                String apiUrl = UriComponentsBuilder.fromUriString(baseUrl)
                        .queryParam("q", q)
                        .queryParam("key", key)
                        .build()
                        .toUriString();
                return handleResponce(apiUrl, baseUrl);
            });
        } catch (RequestNotPermitted ignored){
            return handleRateLimiter();
        }
    }
    private ResponseEntity<Object> handleResponce(String apiUrl, String baseUrl){
        try {
            return handleCorrectResponse(apiUrl, baseUrl);
        }
        catch (WebClientResponseException e){
            return handleErrorResponse(e, baseUrl);
        }
    }

    private ResponseEntity<Object> handleCorrectResponse(String apiUrl, String baseUrl){
        JsonNode jsonResponse = webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        ResponseEntity<Object> responseEntity = jsonCheckKeys(jsonResponse, new String[]{"current", "temp_c"}, baseUrl);
        if (responseEntity == null) {
            return new ResponseEntity<>(jsonResponse.get("current").get("temp_c"), HttpStatus.OK);
        }
        return responseEntity;
    }

    private ResponseEntity<Object> handleErrorResponse(WebClientResponseException e, String baseUrl){
        JsonNode jsonResponse = stringToJson(e.getResponseBodyAsString());
        ResponseEntity<Object> responseEntity = jsonCheckKeys(jsonResponse, new String[]{"error", "message"}, baseUrl);
        if (responseEntity == null) {
            ResponseErrorDto responseErrorDto = new ResponseErrorDto(
                    String.valueOf(jsonResponse.get("error").get("message")).replace("\"", ""),
                    LocalDate.now(),
                    baseUrl
            );
            return new ResponseEntity<>(responseErrorDto, HttpStatus.valueOf(e.getRawStatusCode()));
        }
        return responseEntity;
    }

    private static ResponseEntity<Object> handleRateLimiter(){
        ResponseErrorDto responseErrorDto = new ResponseErrorDto(
                "Too many requests in period",
                LocalDate.now(),
                WeatherApiUrlEnum.CURRENT_WEATHER_URL.getMessage()
        );
        return new ResponseEntity<>(responseErrorDto, HttpStatus.TOO_MANY_REQUESTS);
    }

    private static JsonNode stringToJson(String response){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.isEmpty() ? null : jsonNode;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static ResponseEntity<Object> jsonCheckKeys(JsonNode json, String[] keys, String baseUrl){
        for (String key : keys) {
            if (json == null || json.findPath(key) == null) {
                ResponseErrorDto responseErrorDto = new ResponseErrorDto(
                        "Incorrect response format. The key \"" + key + "\" does not exist in response",
                        LocalDate.now(),
                        baseUrl
                );
                return new ResponseEntity<>(responseErrorDto, HttpStatus.NOT_FOUND);
            }
        }
        return null;
    }
}
