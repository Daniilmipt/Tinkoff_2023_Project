package org.example.services.impl.Hiber;

import org.example.model.Region;
import org.example.model.WeatherNew;
import org.example.model.WeatherType;
import org.example.repositories.Hiber.WeatherModelHiberRepository;
import org.example.services.WeatherNewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class WeatherNewHiberServiceImpl implements WeatherNewService {
    private final WeatherModelHiberRepository weatherModelHiberRepository;
    private final WeatherTypeHiberServiceImpl weatherTypeHiberService;
    private final RegionHiberServiceImpl regionHiberService;

    public WeatherNewHiberServiceImpl(WeatherModelHiberRepository weatherModelHiberRepository, WeatherTypeHiberServiceImpl weatherTypeHiberService, RegionHiberServiceImpl regionHiberService) {
        this.weatherModelHiberRepository = weatherModelHiberRepository;
        this.weatherTypeHiberService = weatherTypeHiberService;
        this.regionHiberService = regionHiberService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void saveByWeatherTypeAndRegion(WeatherType weatherType, Region region, Integer temperature){
        Region regionDataBase = regionHiberService.save(region);
        WeatherType weatherTypeDataBase = weatherTypeHiberService.save(weatherType);
        save(new WeatherNew(regionDataBase.getId(), weatherTypeDataBase.getId(), temperature, LocalDate.now()));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public WeatherNew save(WeatherNew weatherNew){
        Optional<WeatherNew> weatherNewDataBase = weatherModelHiberRepository.findIfExists(weatherNew.getRegion_id(), weatherNew.getDate());
        return weatherNewDataBase.orElseGet(() -> weatherModelHiberRepository.save(weatherNew));
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Optional<WeatherNew> get(Long WeatherModelId){
        return weatherModelHiberRepository.findById(WeatherModelId);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Optional<WeatherNew> getByRegionAndDate(Long region_id, LocalDate date){
        return weatherModelHiberRepository.getWeatherByRegionAndDate(region_id, date);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void deleteByRegion(Long regionId){
        weatherModelHiberRepository.deleteWeatherByRegion(regionId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void deleteByRegionAndDate(Long weatherTypeId, LocalDate date){
        weatherModelHiberRepository.deleteWeatherByRegionAndDate(weatherTypeId, date);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void updateTemperatureByRegionAndDate(Long region_id, Integer temperature, LocalDate date){
        weatherModelHiberRepository.updateTemperatureByRegionAndDate(region_id, temperature, date);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void updateTypeByRegionAndDate(Long region_id, Long type_id, LocalDate date){
        weatherModelHiberRepository.updateTypeByRegionAndDate(region_id, type_id, date);
    }
}
