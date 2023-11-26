package org.example.repositories.Hiber;

import org.example.model.WeatherNew;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherModelHiberRepository extends CrudRepository<WeatherNew, Long> {
    @Query("update WeatherNew wm set wm.temperature = :temperature where wm.region_id = :region_id and wm.date = :date")
    @Modifying
    void updateTemperatureByRegionAndDate(Long region_id, Integer temperature, LocalDate date);

    @Query("update WeatherNew wm set wm.type_id = :type_id where wm.region_id = :region_id and wm.date = :date")
    @Modifying
    void updateTypeByRegionAndDate(Long region_id, Long type_id, LocalDate date);

    @Query("select wm from WeatherNew wm where wm.region_id = :region_id and wm.date = :date")
    Optional<WeatherNew> getWeatherByRegionAndDate(Long region_id, LocalDate date);

    @Query("delete from WeatherNew as w where w.region_id = :region_id")
    @Modifying
    void deleteWeatherByRegion(Long region_id);

    @Query("delete from WeatherNew as w where w.region_id = :region_id and w.date=:date")
    @Modifying
    void deleteWeatherByRegionAndDate(Long region_id, LocalDate date);

    @Query("select wn from WeatherNew as wn where wn.region_id = :region_id and wn.date=:date")
    Optional<WeatherNew> findIfExists(Long region_id, LocalDate date);

    @Query("select count(*) as cnt from WeatherNew as wn where wn.region_id = :region_id and wn.date=:date")
    Integer getRowsCount(Long region_id, LocalDate date);

    @Query("select wn from WeatherNew as wn where wn.region_id = :regionId")
    List<WeatherNew> getWeatherNewsByRegion_id(Long regionId);

    @Query(value = "select wn.temperature from weather as wn where wn.region_id = :regionId order by wn.date desc", nativeQuery = true)
    List<Integer> getLastWeathersByRegion(Long regionId, Pageable pageable);
}
