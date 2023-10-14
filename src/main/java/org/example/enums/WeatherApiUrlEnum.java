package org.example.enums;

import lombok.Getter;

@Getter
public enum WeatherApiUrlEnum {
    CURRENT_WEATHER_URL("/v1/current.json");

    private final String message;

    WeatherApiUrlEnum(String message) {
        this.message = message;
    }
}