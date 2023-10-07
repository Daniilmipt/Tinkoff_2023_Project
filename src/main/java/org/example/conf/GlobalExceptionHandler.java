package org.example.conf;

import org.example.dto.ResponseErrorDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.UnknownHostException;
import java.time.LocalDate;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Value("${weather.api.base-url}")
    String baseUrl;

    @ExceptionHandler(UnknownHostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleUnknownHostException() {
        return handleErrorUrl(baseUrl);
    }

    private static ResponseEntity<Object> handleErrorUrl(String baseUrl){
        ResponseErrorDto responseErrorDto = new ResponseErrorDto(
                "Incorrect base url",
                LocalDate.now(),
                baseUrl
        );
        return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
    }
}
