package org.example.services;

import org.example.dto.WeatherTypeDto;
import org.example.model.WeatherType;

import java.util.Optional;

public interface WeatherTypeService {
    WeatherTypeDto save(WeatherType weatherType) throws SQLException;
    Optional<WeatherTypeDto> get(Long weatherTypeId) throws SQLException;
    void delete(Long weatherTypeId) throws SQLException;
    void update(Long weatherTypeId, String description) throws SQLException;
}
