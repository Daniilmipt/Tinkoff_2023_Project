package org.example.module.service.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.conf.JdbcConfig;
import org.example.enums.jdbc.RegionSql;
import org.example.model.Region;
import org.example.repositories.Hiber.RegionHiberRepository;
import org.example.services.impl.Hiber.RegionHiberServiceImpl;
import org.example.services.impl.JDBC.RegionJdbcServiceImpl;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RegionTest
{

    @InjectMocks
    RegionJdbcServiceImpl regionJdbcService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void save_test() {
        System.out.println(jdbcTemplate);
        Region region = new Region("test");

        when(jdbcTemplate.update(RegionSql.INSERT.getMessage(), "test")).thenReturn(1);
        System.out.println(regionJdbcService.save(region));
//        assertNotNull(regionSaved);
//        assertEquals(region, regionSaved);
//        verify(regionHiberRepository).save(region);
//        verify(regionHiberRepository).findIfExists(region.getName());
    }

//    @Test
//    public void get_ifNotExist() {
//        when(regionHiberRepository.findById(anyLong())).thenReturn(null);
//        assertNull(regionHiberService.get(1L));
//        verify(regionHiberRepository).findById(1L);
//    }
//
//    @Test
//    public void get_ifExist() {
//        Region region = mock(Region.class);
//        when(regionHiberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(region));
//
//        Optional<Region> regionSaved = regionHiberService.get(1L);
//        assertFalse(regionSaved.isEmpty());
//        assertEquals(region, regionSaved.get());
//        verify(regionHiberRepository).findById(1L);
//    }
//
//    @Test
//    public void delete_test(){
//        regionHiberService.delete(1L);
//        verify(regionHiberRepository).deleteById(1L);
//    }
//
//    @Test
//    public void update_test(){
//        when(regionHiberRepository.getRowsCount(anyString())).thenReturn(1);
//        Integer rowsCount = regionHiberService.update(1L, "test");
//        assertEquals(rowsCount, 1);
//        verify(regionHiberRepository).getRowsCount("test");
//        verify(regionHiberRepository).updateRegionById(1L, "test");
//    }
}
