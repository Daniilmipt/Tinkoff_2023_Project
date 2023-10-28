package org.example.module.service.hiber;

import org.example.model.Region;
import org.example.repositories.Hiber.RegionHiberRepository;
import org.example.services.impl.Hiber.RegionHiberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegionTest
{
    @InjectMocks
    private RegionHiberServiceImpl regionHiberService;

    @Mock
    private RegionHiberRepository regionHiberRepository;

    @Test
    public void save_test() {
        Region region = mock(Region.class);

        when(regionHiberRepository.save(ArgumentMatchers.any(Region.class))).thenReturn(region);
        Region regionSaved = regionHiberService.save(region);
        assertNotNull(regionSaved);
        assertEquals(region, regionSaved);
        verify(regionHiberRepository).save(region);
        verify(regionHiberRepository).findIfExists(region.getName());
    }

    @Test
    public void get_ifNotExist() {
        when(regionHiberRepository.findById(anyLong())).thenReturn(null);
        assertNull(regionHiberService.get(1L));
        verify(regionHiberRepository).findById(1L);
    }

    @Test
    public void get_ifExist() {
        Region region = mock(Region.class);
        when(regionHiberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(region));

        Optional<Region> regionSaved = regionHiberService.get(1L);
        assertFalse(regionSaved.isEmpty());
        assertEquals(region, regionSaved.get());
        verify(regionHiberRepository).findById(1L);
    }

    @Test
    public void delete_test(){
        regionHiberService.delete(1L);
        verify(regionHiberRepository).deleteById(1L);
    }

    @Test
    public void update_test(){
        when(regionHiberRepository.getRowsCount(anyString())).thenReturn(1);
        Integer rowsCount = regionHiberService.update(1L, "test");
        assertEquals(rowsCount, 1);
        verify(regionHiberRepository).getRowsCount("test");
        verify(regionHiberRepository).updateRegionById(1L, "test");
    }
}
