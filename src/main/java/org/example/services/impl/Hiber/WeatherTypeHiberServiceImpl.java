package org.example.services.impl.Hiber;

import org.example.dto.WeatherTypeDto;
import org.example.mapper.WeatherTypeMapper;
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

    /*
    REPEATABLE_READ, т.к. не учитываю фантомное чтение,
    потому что в таблицы редко что-то добавляют
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public WeatherTypeDto save(WeatherType weatherType){
        Optional<WeatherType> weatherTypeDataBase = weatherTypeHiberRepository.findWeatherTypeByDescription(weatherType.getDescription());
        return WeatherTypeMapper.entityToDto(weatherTypeDataBase.orElseGet(() -> weatherTypeHiberRepository.save(weatherType)));
    }

    /*
     взял READ_UNCOMMITTED, т.к. в таблицы нечасто вносят изменения посредством update
     */
    @Transactional(isolation= Isolation.READ_UNCOMMITTED, readOnly = true)
    public Optional<WeatherTypeDto> get(Long weatherTypeId){
        return WeatherTypeMapper.optionalEntityToDto(weatherTypeHiberRepository.findById(weatherTypeId));
    }

    /*
    REPEATABLE_READ, т.к. не учитываю фантомное чтение,
    потому что в таблицы редко что-то добавляют
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void delete(Long weatherTypeId){
        weatherTypeHiberRepository.deleteById(weatherTypeId);
    }


    /*
    REPEATABLE_READ, т.к. не учитываю фантомное чтение,
    потому что в таблицы редко что-то добавляют
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void update(Long weatherTypeId, String description){
        weatherTypeHiberRepository.updateWeatherTypeById(weatherTypeId, description);
    }
}
