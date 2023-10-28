package org.example.services;

import org.example.model.Region;
import org.example.model.WeatherNew;
import org.example.model.WeatherType;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherNewService {
    WeatherNew save(WeatherNew weatherNew);
    WeatherNew saveByWeatherTypeAndRegion(WeatherType weatherType, Region region, Integer temperature);
    Optional<WeatherNew> getByRegionAndDate(Long region_id, LocalDate date);
    Optional<WeatherNew> get(Long weatherModel_id);
    void deleteByRegion(Long regionId);
    void deleteByRegionAndDate(Long regionId, LocalDate date);
    Integer updateTemperatureByRegionAndDate(Long region_id, Integer temperature, LocalDate date);
    Integer updateTypeByRegionAndDate(Long region_id, Long type_id, LocalDate date);

}
