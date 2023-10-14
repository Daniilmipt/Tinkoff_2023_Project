package org.example.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class WeatherResponseDto {
    @JsonProperty("error")
    WeatherErrorResponseDto error;
}