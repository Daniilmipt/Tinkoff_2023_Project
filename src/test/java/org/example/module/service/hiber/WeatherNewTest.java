package org.example.module.service.hiber;

import org.example.model.Region;
import org.example.model.WeatherNew;
import org.example.model.WeatherType;
import org.example.repositories.Hiber.WeatherModelHiberRepository;
import org.example.services.impl.Hiber.RegionHiberServiceImpl;
import org.example.services.impl.Hiber.WeatherNewHiberServiceImpl;
import org.example.services.impl.Hiber.WeatherTypeHiberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherNewTest {
    @InjectMocks
    private WeatherNewHiberServiceImpl weatherNewHiberService;

    @Mock
    private WeatherModelHiberRepository weatherModelHiberRepository;

    @Mock
    private RegionHiberServiceImpl regionHiberService;

    @Mock
    private WeatherTypeHiberServiceImpl weatherTypeHiberService;

    @Test
    public void save_test() {
        WeatherNew weatherNew = mock(WeatherNew.class);

        when(weatherModelHiberRepository.save(weatherNew)).thenReturn(weatherNew);
        WeatherNew weatherNewSaved = weatherNewHiberService.save(weatherNew);
        assertNotNull(weatherNewSaved);
        assertEquals(weatherNew, weatherNewSaved);
        verify(weatherModelHiberRepository).save(weatherNew);
        verify(weatherModelHiberRepository).findIfExists(weatherNew.getRegion_id(), weatherNew.getDate());
    }

    @Test
    public void saveByWeatherTypeAndRegion_test() {
        WeatherNew weatherNew = mock(WeatherNew.class);
        Region region = new Region(1L, "test");
        WeatherType weatherType = new WeatherType(1L, "test");

        when(weatherModelHiberRepository.save(ArgumentMatchers.any(WeatherNew.class))).thenReturn(weatherNew);
        when(regionHiberService.save(ArgumentMatchers.any(Region.class))).thenReturn(region);
        when(weatherTypeHiberService.save(ArgumentMatchers.any(WeatherType.class))).thenReturn(weatherType);

        WeatherNew weatherNewSaved = weatherNewHiberService.saveByWeatherTypeAndRegion(
                weatherType,
                region,
                0
        );
        assertNotNull(weatherNewSaved);
        assertEquals(weatherNew, weatherNewSaved);
        verify(weatherModelHiberRepository)
                .findIfExists(anyLong(), ArgumentMatchers.any(LocalDate.class));
        verify(weatherModelHiberRepository).save(ArgumentMatchers.any(WeatherNew.class));
        verify(weatherTypeHiberService).save(weatherType);
        verify(regionHiberService).save(region);
    }

    @Test
    public void get_ifNotExist() {
        when(weatherModelHiberRepository.findById(anyLong())).thenReturn(null);
        assertNull(weatherNewHiberService.get(1L));
        verify(weatherModelHiberRepository).findById(1L);
    }

    @Test
    public void get_ifExist() {
        WeatherNew weatherNew = mock(WeatherNew.class);
        when(weatherModelHiberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(weatherNew));

        Optional<WeatherNew> regionSaved = weatherNewHiberService.get(1L);
        assertFalse(regionSaved.isEmpty());
        assertEquals(weatherNew, regionSaved.get());
        verify(weatherModelHiberRepository).findById(1L);
    }

    @Test
    public void getByRegionAndDate_ifNotExist() {
        LocalDate dateNow = LocalDate.now();

        when(weatherModelHiberRepository.getWeatherByRegionAndDate(anyLong(), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(null);
        assertNull(weatherNewHiberService.getByRegionAndDate(1L, dateNow));
        verify(weatherModelHiberRepository).getWeatherByRegionAndDate(1L, dateNow);
    }

    @Test
    public void getByRegionAndDate_ifExist() {
        LocalDate dateNow = LocalDate.now();

        WeatherNew weatherNew = mock(WeatherNew.class);
        when(weatherModelHiberRepository.getWeatherByRegionAndDate(anyLong(), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(Optional.ofNullable(weatherNew));

        Optional<WeatherNew> weatherNewSaved = weatherNewHiberService.getByRegionAndDate(1L, dateNow);
        assertFalse(weatherNewSaved.isEmpty());
        assertEquals(weatherNew, weatherNewSaved.get());
        verify(weatherModelHiberRepository).getWeatherByRegionAndDate(1L, dateNow);
    }

    @Test
    public void deleteByRegion_test(){
        weatherNewHiberService.deleteByRegion(1L);
        verify(weatherModelHiberRepository).deleteWeatherByRegion(1L);
    }

    @Test
    public void deleteByRegionAndDate_test(){
        LocalDate dateNow = LocalDate.now();

        weatherNewHiberService.deleteByRegionAndDate(1L, dateNow);
        verify(weatherModelHiberRepository).deleteWeatherByRegionAndDate(1L, dateNow);
    }

    @Test
    public void updateTemperatureByRegionAndDate(){
        LocalDate dateNow = LocalDate.now();

        when(weatherModelHiberRepository.getRowsCount(anyLong(), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(1);
        Integer rowsCount = weatherNewHiberService.updateTemperatureByRegionAndDate(1L, 0, dateNow);
        assertEquals(rowsCount, 1);
        verify(weatherModelHiberRepository).getRowsCount(1L, dateNow);
        verify(weatherModelHiberRepository).updateTemperatureByRegionAndDate(1L, 0, dateNow);
    }
}
