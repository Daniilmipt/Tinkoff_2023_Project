package org.example.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@Builder
public record ResponseErrorDto(
        String error,
        LocalDate timestamp,
        String path
) {
    public static ResponseEntity<Object> getErrorResponse(String message, String url, HttpStatus status){
        ResponseErrorDto responseErrorDto = new ResponseErrorDto(
                message,
                LocalDate.now(),
                url
        );
        return new ResponseEntity<>(responseErrorDto, status);
    }
}
