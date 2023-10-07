package org.example.exceptions.enums;

import lombok.Getter;

@Getter
public enum OperationException {
    WEATHER_ADD_AGAIN("This object was added");

    private final String message;

    OperationException(String message) {
        this.message = message;
    }
}
