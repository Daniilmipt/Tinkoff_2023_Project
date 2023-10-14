package org.example.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.api.WeatherErrorResponseDto;
import org.example.dto.api.WeatherResponseDto;
import org.example.exceptions.weatherApi.JsonException;

public class WeatherApiValid {
    public static void jsonCheckCurrent(JsonNode json){
        if (json == null || json.findPath("current") == null){
            throw new JsonException("Incorrect response format. Can not convert to Json");
        }
        if (json.findPath("current").findPath("temp_c") == null){
            throw new JsonException("Incorrect response format. Can not convert to Json");
        }
    }

    public static WeatherErrorResponseDto getErrorResponseDto(String response){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            WeatherResponseDto weatherDto = objectMapper.readValue(response, WeatherResponseDto.class);
            if (weatherDto.getError().getMessage() == null){
                throw new JsonException("Incorrect response format. Can not convert to Json");
            }
            return weatherDto.getError();
        } catch (Exception ignored) {
            throw new JsonException("Incorrect response format. Can not convert to Json");
        }
    }
}