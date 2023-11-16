package org.example.repositories.Hiber;

import org.example.model.WeatherNew;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeatherModelHiberRepository extends CrudRepository<WeatherNew, Long> {
    @Query("update WeatherNew wm set wm.temperature = :temperature where wm.regionId = :regionId and wm.date = :date")
    @Modifying
    void updateTemperatureByRegionAndDate(Long regionId, Integer temperature, LocalDate date);

    @Query("update WeatherNew wm set wm.typeId = :typeId where wm.regionId = :regionId and wm.date = :date")
    @Modifying
    void updateTypeByRegionAndDate(Long regionId, Long typeId, LocalDate date);

    Optional<WeatherNew> getWeatherNewByDateAndRegionId(LocalDate date, Long regionId);
    void deleteWeatherNewByRegionId(Long regionId);
    void deleteWeatherNewByDateAndRegionId(LocalDate date, Long regionId);
}
