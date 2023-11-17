package org.example.module.service.hiber;

import org.example.cache.WeatherCaches;
import org.example.exceptions.CacheException;
import org.example.model.Region;
import org.example.model.WeatherNew;
import org.example.model.WeatherType;
import org.example.repositories.Hiber.WeatherModelHiberRepository;
import org.example.services.impl.Hiber.RegionHiberServiceImpl;
import org.example.services.impl.Hiber.WeatherNewHiberServiceImpl;
import org.example.services.impl.Hiber.WeatherTypeHiberServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class WeatherCachesTest {

    @InjectMocks
    private WeatherNewHiberServiceImpl weatherNewHiberService;

    @Mock
    private WeatherModelHiberRepository weatherModelHiberRepository;

    @Mock
    private RegionHiberServiceImpl regionHiberService;

    @Mock
    private WeatherTypeHiberServiceImpl weatherTypeHiberService;

    private static WeatherNew weatherNew;
    private static WeatherCaches weatherCaches;
    private static String regionName;

    @BeforeAll
    public static void setWeather(){
        weatherCaches = WeatherCaches.getInstance();
        ReflectionTestUtils.setField(weatherCaches, "size", 1000L);
        ReflectionTestUtils.setField(weatherCaches, "duration", 10L);

        regionName = "London";
        weatherNew = new WeatherNew(1L, 1L, 1L, 30, LocalDate.now());
    }

    @BeforeEach
    public void setCache(){
        weatherCaches.setWeather(regionName, weatherNew);
    }

    @Test
    public void get_NotExistsIn_Cache() {
        assertThrows(CacheException.class, () -> weatherCaches.getWeatherObject(""));
    }

    @Test
    public void get_ExistsIn_Cache() {
        assertEquals(weatherCaches.getWeatherObject(regionName).getWeatherNew(), weatherNew);
    }


    @Test
    public void save_NotExistsIn_Cache() {
        assertTrue(weatherCaches.ifExist(regionName));
    }

    @Test
    public void save_ExistsIn_Cache() {
        Region region = new Region(1L, "London");
        WeatherType weatherType = new WeatherType(1L, "test");

        WeatherNew weatherNewSaved = weatherNewHiberService.saveByWeatherTypeAndRegion(
                weatherType,
                region,
                0
        );
        assertEquals(weatherNewSaved, weatherCaches.getWeatherObject(regionName).getWeatherNew());
        verify(weatherModelHiberRepository, never())
                .findIfExists(anyLong(), ArgumentMatchers.any(LocalDate.class));
        verify(weatherModelHiberRepository, never()).save(ArgumentMatchers.any(WeatherNew.class));
        verify(weatherTypeHiberService, never()).save(weatherType);
        verify(regionHiberService, never()).save(region);
    }

    @Test
    public void deleteByRegion_RemoveFrom_Cache(){

        when(weatherModelHiberRepository.getWeatherNewsByRegion_id(anyLong()))
                .thenReturn(Collections.singletonList(weatherCaches.getWeatherObject(regionName).getWeatherNew()));

        weatherNewHiberService.deleteByRegion(1L);
        assertTrue(
                weatherCaches.getMapCache().entrySet()
                .stream()
                .filter(entry -> entry.getValue().getData().getWeatherNew().getRegion_id() == 1L)
                .findAny()
                .isEmpty()
        );
        verify(weatherModelHiberRepository).deleteWeatherByRegion(1L);
    }

    @Test
    public void deleteByRegionAndDate_RemoveFrom_Cache(){
        LocalDate dateNow = LocalDate.now();

        when(weatherModelHiberRepository.getWeatherByRegionAndDate(anyLong(), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(Optional.of(weatherCaches.getWeatherObject(regionName).getWeatherNew()));

        weatherNewHiberService.deleteByRegionAndDate(1L, dateNow);
        assertTrue(
                weatherCaches.getMapCache().entrySet()
                        .stream()
                        .filter(entry -> entry.getValue().getData().getWeatherNew().getRegion_id() == 1L)
                        .findAny()
                        .isEmpty()
        );
        verify(weatherModelHiberRepository).deleteWeatherByRegionAndDate(1L, dateNow);
    }

    @Test
    public void updateTemperatureByRegionAndDate_Update_Cache(){
        LocalDate dateNow = LocalDate.now();
        WeatherNew weatherNewTest = new WeatherNew(1L, 1L, 1L, 30, LocalDate.now());

        when(weatherModelHiberRepository.getRowsCount(anyLong(), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(1);
        when(regionHiberService.get(anyLong())).thenReturn(Optional.of(new Region(1L, regionName)));
        when(weatherModelHiberRepository.getWeatherByRegionAndDate(anyLong(), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(Optional.of(weatherNewTest));

        weatherNewHiberService.updateTemperatureByRegionAndDate(1L, 0, dateNow);
        assertEquals(weatherCaches.getWeatherObject(regionName).getWeatherNew(), weatherNewTest);
        verify(weatherModelHiberRepository).getRowsCount(1L, dateNow);
        verify(weatherModelHiberRepository).updateTemperatureByRegionAndDate(1L, 0, dateNow);
    }

}
