package org.example.services;

import org.springframework.http.ResponseEntity;

public interface WeatherApiService {
    ResponseEntity<Object> getCurrentTemperature(String q);
}
