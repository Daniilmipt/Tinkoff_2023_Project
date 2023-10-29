package org.example.services.impl.Hiber;

import org.example.model.WeatherType;
import org.example.repositories.Hiber.WeatherTypeHiberRepository;
import org.example.services.WeatherTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class WeatherTypeHiberServiceImpl implements WeatherTypeService {
    private final WeatherTypeHiberRepository weatherTypeHiberRepository;

    public WeatherTypeHiberServiceImpl(WeatherTypeHiberRepository weatherTypeHiberRepository) {
        this.weatherTypeHiberRepository = weatherTypeHiberRepository;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public WeatherType save(WeatherType weatherType){
        Optional<WeatherType> weatherTypeDataBase = weatherTypeHiberRepository.findIfExists(weatherType.getDescription());
        return weatherTypeDataBase.orElseGet(() -> weatherTypeHiberRepository.save(weatherType));
    }

    @Transactional(isolation= Isolation.READ_UNCOMMITTED, readOnly = true)
    public Optional<WeatherType> get(Long weatherTypeId){
        return weatherTypeHiberRepository.findById(weatherTypeId);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void delete(Long weatherTypeId){
        weatherTypeHiberRepository.deleteById(weatherTypeId);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void update(Long weatherTypeId, String description){
        weatherTypeHiberRepository.updateWeatherTypeById(weatherTypeId, description);
    }
}
