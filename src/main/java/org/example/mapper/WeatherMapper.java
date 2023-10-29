package org.example.mapper;

import org.example.dto.WeatherDto;
import org.example.model.WeatherNew;

import java.util.Optional;

public class WeatherMapper {
    public static WeatherDto entityToDto(WeatherNew entity){
        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setId(entity.getId());
        weatherDto.setRegionId(entity.getRegionId());
        weatherDto.setTypeId(entity.getTypeId());
        weatherDto.setTemperature(entity.getTemperature());
        weatherDto.setDate(entity.getDate());
        return weatherDto;
    }

    public static Optional<WeatherDto> optionalEntityToDto(Optional<WeatherNew> entity){
        if (entity.isEmpty()){
            return Optional.empty();
        }
        WeatherDto weatherDto = new WeatherDto();
        WeatherNew weatherNew = entity.get();
        
        weatherDto.setId(weatherNew.getId());
        weatherDto.setRegionId(weatherNew.getRegionId());
        weatherDto.setTypeId(weatherNew.getTypeId());
        weatherDto.setTemperature(weatherNew.getTemperature());
        weatherDto.setDate(weatherNew.getDate());
        return Optional.of(weatherDto);
    }
}