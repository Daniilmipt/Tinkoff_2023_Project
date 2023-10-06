package org.example;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Weather {
    private Long id;
    private String regionName;
    private Integer temperature;
    private LocalDate date;
}
