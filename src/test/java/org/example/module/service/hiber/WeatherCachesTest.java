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
        regionName = "London";
        weatherNew = new WeatherNew(1L, 1L, 1L, 30, LocalDate.now());
        weatherCaches = WeatherCaches.getInstance();

        ReflectionTestUtils.setField(weatherCaches, "size", 1000L);
        ReflectionTestUtils.setField(weatherCaches, "duration", 60L);
    }

    @BeforeEach
    public void setCache(){
        weatherCaches.clearCache();
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
    public void get_TimeOutIn_Cache() throws InterruptedException {
        ReflectionTestUtils.setField(weatherCaches, "duration", 10L);

        assertEquals(weatherCaches.getWeatherObject(regionName).getWeatherNew(), weatherNew);
        Thread.sleep(10000);
        assertThrows(CacheException.class, () -> weatherCaches.getWeatherObject(regionName));

        ReflectionTestUtils.setField(weatherCaches, "duration", 60L);
    }


    @Test
    public void save_NotExistsIn_Cache_Order() throws InterruptedException {
        WeatherNew weatherNew_test = new WeatherNew(1L, 1L, 1L, 60, LocalDate.now());
        Thread.sleep(3000);
        weatherCaches.setWeather("Moscow", weatherNew_test);

        ReflectionTestUtils.setField(weatherCaches, "duration", 10L);
        Thread.sleep(8000);
        assertDoesNotThrow(() -> weatherCaches.getWeatherObject("Moscow").getWeatherNew());
        assertThrows(CacheException.class, () -> weatherCaches.getWeatherObject(regionName));

        ReflectionTestUtils.setField(weatherCaches, "duration", 60L);
    }


    @Test
    public void save_NotExistsIn_Cache_Overload() {
        ReflectionTestUtils.setField(weatherCaches, "size", 1L);
        Region region = new Region(1L, "Moscow");
        WeatherType weatherType = new WeatherType(1L, "ignored");

        when(weatherModelHiberRepository.save(ArgumentMatchers.any(WeatherNew.class))).thenReturn(weatherNew);
        when(regionHiberService.save(ArgumentMatchers.any(Region.class))).thenReturn(region);
        when(weatherTypeHiberService.save(ArgumentMatchers.any(WeatherType.class))).thenReturn(weatherType);

        weatherNewHiberService.saveByWeatherTypeAndRegion(weatherType, region, 0);

        assertThrows(CacheException.class, () -> weatherCaches.getWeatherObject(regionName));
        assertDoesNotThrow(() -> weatherCaches.getWeatherObject("Moscow").getWeatherNew());
        assertEquals(weatherCaches.getCache().getSize(), 1);
        assertEquals(weatherCaches.getMapCache().size(), 1);

        verify(weatherModelHiberRepository)
                .findIfExists(anyLong(), ArgumentMatchers.any(LocalDate.class));
        verify(weatherModelHiberRepository).save(ArgumentMatchers.any(WeatherNew.class));
        verify(weatherTypeHiberService).save(weatherType);
        verify(regionHiberService).save(region);

        ReflectionTestUtils.setField(weatherCaches, "size", 1000L);
    }

    @Test
    public void save_NotExistsIn_Cache() {
        WeatherNew weatherNew_test = mock(WeatherNew.class);
        Region region = new Region(1L, "Moscow");
        WeatherType weatherType = new WeatherType(1L, "ignored");

        when(weatherModelHiberRepository.save(ArgumentMatchers.any(WeatherNew.class))).thenReturn(weatherNew_test);
        when(regionHiberService.save(ArgumentMatchers.any(Region.class))).thenReturn(region);
        when(weatherTypeHiberService.save(ArgumentMatchers.any(WeatherType.class))).thenReturn(weatherType);

        weatherNewHiberService.saveByWeatherTypeAndRegion(weatherType, region, 0);
        assertDoesNotThrow(() -> weatherCaches.getWeatherObject("Moscow").getWeatherNew());
        assertEquals(weatherCaches.getCache().getSize(), 2);
        assertEquals(weatherCaches.getMapCache().size(), 2);

        verify(weatherModelHiberRepository)
                .findIfExists(anyLong(), ArgumentMatchers.any(LocalDate.class));
        verify(weatherModelHiberRepository).save(ArgumentMatchers.any(WeatherNew.class));
        verify(weatherTypeHiberService).save(weatherType);
        verify(regionHiberService).save(region);
    }

    @Test
    public void save_ExistsIn_Cache() {
        Region region = new Region(1L, "London");
        WeatherType weatherType = new WeatherType(1L, "ignored");

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
