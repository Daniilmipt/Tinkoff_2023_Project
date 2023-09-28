package org.example.services.impl;

import org.example.Weather;
import org.example.repositories.WeatherRepository;
import org.example.services.WeatherService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {
    private final WeatherRepository weatherRepository;

    public WeatherServiceImpl(WeatherRepository weatherRepository){
        this.weatherRepository = weatherRepository;
    }

    @Override
    public List<Integer> get(String regionName, Date dateTime) {
        return weatherRepository.getTemperature(regionName, dateTime);
    }

    @Override
    public Optional<Weather> add(String regionName, Integer temperature, Date dateTime) {
        return weatherRepository.addRegion(regionName, temperature, dateTime);
    }

    @Override
    public List<Weather> update(String regionName, Integer temperature, Date dateTime) {
        return weatherRepository.updateWeather(regionName, temperature, dateTime);
    }

    @Override
    public Optional<List<Weather>> delete(String regionName) {
        return weatherRepository.deleteWeather(regionName);
    }
}
