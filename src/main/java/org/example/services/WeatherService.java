package org.example.services;

import org.example.Weather;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeatherService {
    List<Integer> get(String regionName, LocalDate date);
    Optional<Weather> add(String regionName, Integer temperature, LocalDate date);
    Integer updateTempetatureByRegionAndDatetime(String regionName,
                                                 Integer temperature,
                                                 LocalDate date);
    void deleteWeatherByRegionName(String regionName);
}
