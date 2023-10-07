package org.example.exceptions.enums;

import lombok.Getter;

@Getter
public enum WeatherValidException {
    DateTime_NULL("DateTime can not be null"),
    TEMPERATURE_NULL("Temperature can not be null"),
    INCORRECT_DateTime("Incorrect dateTime format"),
    INCORRECT_TEMPERATURE("Temperature must be integer");

    private final String message;

    WeatherValidException(String message) {
        this.message = message;
    }
}
