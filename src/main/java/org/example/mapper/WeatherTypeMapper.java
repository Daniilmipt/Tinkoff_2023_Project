package org.example.mapper;

import org.example.dto.WeatherTypeDto;
import org.example.model.WeatherType;

import java.util.Optional;
public class WeatherTypeMapper {
    public static WeatherTypeDto entityToDto(WeatherType entity){
        WeatherTypeDto weatherTypeDto = new WeatherTypeDto();
        weatherTypeDto.setId(entity.getId());
        weatherTypeDto.setDescription(entity.getDescription());

        return weatherTypeDto;
    }

    public static Optional<WeatherTypeDto> optionalEntityToDto(Optional<WeatherType> entity){
        if (entity.isEmpty()){
            return Optional.empty();
        }
        WeatherTypeDto weatherTypeDto = new WeatherTypeDto();
        weatherTypeDto.setId(entity.get().getId());
        weatherTypeDto.setDescription(entity.get().getDescription());

        return Optional.of(weatherTypeDto);
    }
}
