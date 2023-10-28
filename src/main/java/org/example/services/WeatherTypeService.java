package org.example.services;

import org.example.model.WeatherType;

import java.util.Optional;

public interface WeatherTypeService {
    WeatherType save(WeatherType weatherType);
    Optional<WeatherType> get(Long weatherTypeId);
    void delete(Long weatherTypeId);
    Integer update(Long weatherTypeId, String description);
}
