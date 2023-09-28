package org.example.services;

import org.example.Weather;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface WeatherService {
    List<Integer> get(String regionName, Date dateTime);
    Optional<Weather> add(String regionName, Integer temperature, Date dateTime);
    List<Weather> update(String regionName, Integer temperature, Date dateTime);
    Optional<List<Weather>> delete(String regionName);
}
