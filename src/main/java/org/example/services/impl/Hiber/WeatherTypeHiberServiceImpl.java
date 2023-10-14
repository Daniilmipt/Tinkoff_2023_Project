package org.example.services.impl.Hiber;

import org.example.model.WeatherType;
import org.example.repositories.Hiber.WeatherTypeHiberRepository;
import org.example.services.WeatherTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class WeatherTypeHiberServiceImpl implements WeatherTypeService {
    private final WeatherTypeHiberRepository weatherTypeHiberRepository;

    public WeatherTypeHiberServiceImpl(WeatherTypeHiberRepository weatherTypeHiberRepository) {
        this.weatherTypeHiberRepository = weatherTypeHiberRepository;
    }

    @Transactional
    @Override
    public WeatherType save(WeatherType weatherType){
        return weatherTypeHiberRepository.save(weatherType);
    }

    @Override
    public Optional<WeatherType> get(Long weatherTypeId){
        return weatherTypeHiberRepository.findById(weatherTypeId);
    }

    @Transactional
    @Override
    public void delete(Long weatherTypeId){
        weatherTypeHiberRepository.deleteById(weatherTypeId);
    }

    @Transactional
    @Override
    public void update(Long weatherTypeId, String description){
        weatherTypeHiberRepository.updateWeatherTypeById(weatherTypeId, description);
    }
}
