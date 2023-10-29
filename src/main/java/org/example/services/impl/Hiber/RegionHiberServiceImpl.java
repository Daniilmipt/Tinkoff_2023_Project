package org.example.services.impl.Hiber;

import org.example.model.Region;
import org.example.repositories.Hiber.RegionHiberRepository;
import org.example.services.RegionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegionHiberServiceImpl implements RegionService {
    private final RegionHiberRepository regionHiberRepository;

    public RegionHiberServiceImpl(RegionHiberRepository regionHiberRepository) {
        this.regionHiberRepository = regionHiberRepository;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public Region save(Region region){
        Optional<Region> regionDataBase = regionHiberRepository.findIfExists(region.getName());
        return regionDataBase.orElseGet(() -> regionHiberRepository.save(region));
    }

    @Transactional(isolation= Isolation.READ_UNCOMMITTED, readOnly = true)
    @Override
    public Optional<Region> get(Long regionId){
        return regionHiberRepository.findById(regionId);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void delete(Long regionId){
        regionHiberRepository.deleteById(regionId);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void update(Long regionId, String name){
        regionHiberRepository.updateRegionById(regionId, name);
    }

}
