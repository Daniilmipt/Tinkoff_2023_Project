package org.example.services;

import com.fasterxml.jackson.databind.JsonNode;

public interface WeatherClientService {
    JsonNode getCurrentTemperature(String location);
}