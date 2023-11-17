package org.example.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CacheException extends RuntimeException {
    @JsonProperty("message")
    private final String message;
    @JsonProperty("exception_type")
    private final String exceptionType = "CacheException.class";
    public CacheException(String message) {
        super(message);
        this.message = message;
    }
}
