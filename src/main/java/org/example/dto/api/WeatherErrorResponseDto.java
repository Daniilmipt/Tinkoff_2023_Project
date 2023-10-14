package org.example.dto.api;

import lombok.Getter;

@Getter
public class WeatherErrorResponseDto {
    String message;
    Integer code;
}