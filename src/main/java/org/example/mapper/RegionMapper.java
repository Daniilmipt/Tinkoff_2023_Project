package org.example.mapper;

import org.example.dto.RegionDto;
import org.example.model.Region;

import java.util.Optional;

public class RegionMapper {
    public static RegionDto entityToDto(Region entity){
        RegionDto regionDto = new RegionDto();
        regionDto.setId(entity.getId());
        regionDto.setName(entity.getName());

        return regionDto;
    }

    public static Optional<RegionDto> optionalEntityToDto(Optional<Region> entity){
        if (entity.isEmpty()){
            return Optional.empty();
        }
        RegionDto regionDto = new RegionDto();
        regionDto.setId(entity.get().getId());
        regionDto.setName(entity.get().getName());

        return Optional.of(regionDto);
    }
}
