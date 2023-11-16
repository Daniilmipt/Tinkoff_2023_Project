package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RegionDto {
    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("name")
    private String name;
}
