package org.example.services.impl;

import org.example.Weather;
import org.example.repositories.WeatherRepository;
import org.example.services.WeatherService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {
    private final WeatherRepository weatherRepository;

    public WeatherServiceImpl(WeatherRepository weatherRepository){
        this.weatherRepository = weatherRepository;
    }

    @Override
    public List<Integer> get(String regionName, LocalDate date) {
        return weatherRepository.getTemperature(regionName, date);
    }

    @Override
    public Optional<Weather> add(String regionName, Integer temperature, LocalDate date) {
        return weatherRepository.addRegion(regionName, temperature, date);
    }

    @Override
    public Integer updateTempetatureByRegionAndDatetime(String regionName,
                                                        Integer temperature,
                                                        LocalDate date) {
        return weatherRepository.updateWeather(regionName, temperature, date);
    }

    @Override
    public void deleteWeatherByRegionName(String regionName) {
        weatherRepository.deleteWeather(regionName);
    }
}
