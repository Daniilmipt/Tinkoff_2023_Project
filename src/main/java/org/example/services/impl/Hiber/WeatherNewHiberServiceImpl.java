package org.example.services.impl.Hiber;

import org.example.model.WeatherNew;
import org.example.repositories.Hiber.WeatherModelHiberRepository;
import org.example.services.WeatherNewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class WeatherNewHiberServiceImpl implements WeatherNewService {
    private final WeatherModelHiberRepository weatherModelHiberRepository;

    public WeatherNewHiberServiceImpl(WeatherModelHiberRepository weatherModelHiberRepository) {
        this.weatherModelHiberRepository = weatherModelHiberRepository;
    }

    @Transactional
    @Override
    public WeatherNew save(WeatherNew weatherNew){
        return weatherModelHiberRepository.save(weatherNew);
    }

    @Override
    public Optional<WeatherNew> get(Long WeatherModelId){
        return weatherModelHiberRepository.findById(WeatherModelId);
    }

    @Override
    public Optional<WeatherNew> getByRegionAndDate(Long region_id, LocalDate date){
        return weatherModelHiberRepository.getWeatherByRegionAndDate(region_id, date);
    }

    @Transactional
    @Override
    public void deleteByRegion(Long regionId){
        weatherModelHiberRepository.deleteWeatherByRegion(regionId);
    }

    @Transactional
    @Override
    public void deleteByRegionAndDate(Long weatherTypeId, LocalDate date){
        weatherModelHiberRepository.deleteWeatherByRegionAndDate(weatherTypeId, date);
    }

    @Transactional
    @Override
    public void updateTemperatureByRegionAndDate(Long region_id, Integer temperature, LocalDate date){
        weatherModelHiberRepository.updateTemperatureByRegionAndDate(region_id, temperature, date);
    }

    @Transactional
    @Override
    public void updateTypeByRegionAndDate(Long region_id, Long type_id, LocalDate date){
        weatherModelHiberRepository.updateTypeByRegionAndDate(region_id, type_id, date);
    }
}
