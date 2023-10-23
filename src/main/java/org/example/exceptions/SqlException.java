package org.example.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SqlException extends RuntimeException{
    private final String message;
    private final String url;
    private final HttpStatus httpStatus;
    public SqlException(String message, String url, int httpStatus) {
        super(message);
        this.httpStatus = HttpStatus.valueOf(httpStatus);
        this.message = message;
        this.url = url;
    }
}