package org.example.services;

import org.example.model.Region;
import org.example.model.WeatherNew;
import org.example.model.WeatherType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public interface WeatherNewService {
    WeatherNew save(WeatherNew weatherNew) throws SQLException;
    WeatherNew saveByWeatherTypeAndRegion(WeatherType weatherType, Region region, Integer temperature) throws SQLException;
    Optional<WeatherNew> getByRegionAndDate(Long region_id, LocalDate date) throws SQLException;
    Optional<WeatherNew> get(Long weatherModel_id) throws SQLException;
    void deleteByRegion(Long regionId) throws SQLException;
    void deleteByRegionAndDate(Long regionId, LocalDate date) throws SQLException;
    void updateTemperatureByRegionAndDate(Long region_id, Integer temperature, LocalDate date) throws SQLException;
    void updateTypeByRegionAndDate(Long region_id, Long type_id, LocalDate date) throws SQLException;

}
