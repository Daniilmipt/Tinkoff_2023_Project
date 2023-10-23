package org.example.services;

import org.example.model.Region;

import java.util.Optional;

public interface RegionService {
    Region save(Region region);
    Optional<Region> get(Long regionId);
    void delete(Long regionId);
    void update(Long regionId, String name);
}
