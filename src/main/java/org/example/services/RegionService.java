package org.example.services;

import org.example.dto.RegionDto;
import org.example.model.Region;

import java.sql.SQLException;
import java.util.Optional;

public interface RegionService {
    RegionDto save(Region region) throws SQLException;
    Optional<RegionDto> get(Long regionId) throws SQLException ;
    void delete(Long regionId) throws SQLException ;
    void update(Long regionId, String name) throws SQLException;
}
