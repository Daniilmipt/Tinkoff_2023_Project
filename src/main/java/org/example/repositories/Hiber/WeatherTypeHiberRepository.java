package org.example.repositories.Hiber;

import org.example.model.WeatherType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherTypeHiberRepository extends CrudRepository<WeatherType, Long> {
    @Query("update WeatherType a set a.description = :description where a.id = :id")
    @Modifying
    void updateWeatherTypeById(Long id, String description);

    @Query("select wt from WeatherType wt where wt.description = :description")
    Optional<WeatherType> findIfExists(String description);
}
