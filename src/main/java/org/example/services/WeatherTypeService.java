package org.example.services;

import org.example.model.WeatherType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public interface WeatherTypeService {
    WeatherType save(WeatherType weatherType) throws SQLException;
    Optional<WeatherType> get(Long weatherTypeId) throws SQLException;
    void delete(Long weatherTypeId) throws SQLException;
    void update(Long weatherTypeId, String description) throws SQLException;
}
