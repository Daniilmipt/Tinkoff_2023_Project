package org.example.services.impl.Hiber;

import org.example.dto.RegionDto;
import org.example.dto.WeatherDto;
import org.example.dto.WeatherTypeDto;
import org.example.mapper.WeatherMapper;
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

    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public WeatherDto saveByWeatherTypeAndRegion(WeatherType weatherType, Region region, Integer temperature){
        RegionDto regionDataBase = regionHiberService.save(region);
        WeatherTypeDto weatherTypeDataBase = weatherTypeHiberService.save(weatherType);
        return save(new WeatherNew(regionDataBase.getId(), weatherTypeDataBase.getId(), temperature, LocalDate.now()));
    }

    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public WeatherDto save(WeatherNew weatherNew){
        Optional<WeatherNew> weatherNewDataBase = weatherModelHiberRepository.getWeatherNewByDateAndRegionId(weatherNew.getDate(), weatherNew.getRegionId());
        return WeatherMapper.entityToDto(weatherNewDataBase.orElseGet(() -> weatherModelHiberRepository.save(weatherNew)));
    }

    /*
     взял READ_UNCOMMITTED, т.к. в таблицы нечасто вносят изменения посредством update
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Optional<WeatherDto> get(Long WeatherModelId){
        return WeatherMapper.optionalEntityToDto(weatherModelHiberRepository.findById(WeatherModelId));
    }

    /*
     взял READ_UNCOMMITTED, т.к. в таблицы нечасто вносят изменения посредством update
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Optional<WeatherDto> getByRegionAndDate(Long region_id, LocalDate date){
        return WeatherMapper.optionalEntityToDto(weatherModelHiberRepository.getWeatherNewByDateAndRegionId(date, region_id));
    }

    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void deleteByRegion(Long regionId){
        weatherModelHiberRepository.deleteWeatherNewByRegionId(regionId);
    }


    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void deleteByRegionAndDate(Long regionId, LocalDate date){
        weatherModelHiberRepository.deleteWeatherNewByDateAndRegionId(date, regionId);
    }


    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void updateTemperatureByRegionAndDate(Long regionId, Integer temperature, LocalDate date){
        weatherModelHiberRepository.updateTemperatureByRegionAndDate(regionId, temperature, date);
    }


    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void updateTypeByRegionAndDate(Long regionId, Long typeId, LocalDate date){
        weatherModelHiberRepository.updateTypeByRegionAndDate(regionId, typeId, date);
    }
}
