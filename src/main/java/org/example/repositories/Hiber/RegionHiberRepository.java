package org.example.repositories.Hiber;

import org.example.model.Region;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionHiberRepository extends CrudRepository<Region, Long> {
    @Query("update Region a set a.name = :name where a.id = :id")
    @Modifying
    void updateRegionById(Long id, String name);
}
