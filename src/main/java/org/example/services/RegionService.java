package org.example.services;

import org.example.model.Region;

import java.sql.SQLException;
import java.util.Optional;

public interface RegionService {
    Region save(Region region) throws SQLException;
    Optional<Region> get(Long regionId) throws SQLException ;
    void delete(Long regionId) throws SQLException ;
    void update(Long regionId, String name) throws SQLException;
}
