package org.example.exceptions;

import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {
    private final String message;
    private final String url;
    public AuthorizationException(String message, String url) {
        super(message);
        this.message = message;
        this.url = url;
    }
}
