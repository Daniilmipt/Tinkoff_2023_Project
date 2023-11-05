package org.example.conf;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.example.dto.ResponseErrorDto;
import org.example.dto.api.WeatherErrorResponseDto;
import org.example.exceptions.weatherApi.JsonException;
import org.example.exceptions.weatherApi.ResponseException;
import org.example.exceptions.SqlException;
import org.example.validation.WeatherApiValid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.UnknownHttpStatusCodeException;

import java.net.UnknownHostException;


@RestControllerAdvice
public class WeatherApiExceptionHandler {
    @Value("${weather.api.base-url}")
    String baseUrl;
    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<Object> handleUnknownHostException() {
        return ResponseErrorDto.getErrorResponse(
                "Incorrect base url",
                baseUrl,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResponseException.class)
    public ResponseEntity<Object> handleWebClientResponseException(ResponseException e) {
        WeatherErrorResponseDto weatherDto = WeatherApiValid.getErrorResponseDto(e.getMessage());
        return ResponseErrorDto.getErrorResponse(
                String.valueOf(weatherDto.getMessage()),
                e.getUrl(),
                e.getHttpStatus()
        );
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<Object> handleRequestNotPermitted() {
        return ResponseErrorDto.getErrorResponse(
                "Too many requests in period",
                baseUrl,
                HttpStatus.TOO_MANY_REQUESTS
        );
    }

    @ExceptionHandler(JsonException.class)
    public ResponseEntity<Object> handleJsonException(JsonException jsonException) {
        return ResponseErrorDto.getErrorResponse(
                jsonException.getMessage(),
                baseUrl,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(UnknownHttpStatusCodeException.class)
    public ResponseEntity<Object> handleUnknownCodeException(UnknownHttpStatusCodeException e){
        return ResponseErrorDto.getErrorResponse(
                e.getMessage(),
                baseUrl,
                HttpStatus.valueOf(e.getRawStatusCode())
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException e){
        return ResponseErrorDto.getErrorResponse(
                e.getMessage(),
                "sql_error",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Object> handleEmptyResultDataAccessException(IllegalStateException e){
        return ResponseErrorDto.getErrorResponse(
                e.getMessage(),
                "database error",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(SqlException.class)
    public ResponseEntity<Object> handleSqlException(SqlException e){
        return ResponseErrorDto.getErrorResponse(
                e.getMessage(),
                e.getUrl(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
