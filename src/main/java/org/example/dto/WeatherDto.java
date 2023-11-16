package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class WeatherDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("region_id")
    private Long regionId;

    @JsonProperty("type_id")
    private Long typeId;

    @JsonProperty("temperature")
    private Integer temperature;

    @JsonProperty("date")
    private LocalDate date;
}
