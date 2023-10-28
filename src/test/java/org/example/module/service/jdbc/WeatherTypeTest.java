package org.example.module.service.jdbc;

import org.example.model.WeatherType;
import org.example.repositories.Hiber.WeatherTypeHiberRepository;
import org.example.services.impl.Hiber.WeatherTypeHiberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherTypeTest {
    @InjectMocks
    private WeatherTypeHiberServiceImpl weatherTypeHiberService;

    @Mock
    private WeatherTypeHiberRepository weatherTypeHiberRepository;

    @Test
    public void save_test() {
        WeatherType weatherType = mock(WeatherType.class);

        when(weatherTypeHiberRepository.save(ArgumentMatchers.any(WeatherType.class))).thenReturn(weatherType);
        WeatherType weatherTypeSaved = weatherTypeHiberService.save(weatherType);
        assertNotNull(weatherTypeSaved);
        assertEquals(weatherType, weatherTypeSaved);
        verify(weatherTypeHiberRepository).save(weatherType);
        verify(weatherTypeHiberRepository).findIfExists(weatherType.getDescription());
    }

    @Test
    public void get_test() {
        when(weatherTypeHiberRepository.findById(anyLong())).thenReturn(null);
        assertNull(weatherTypeHiberService.get(1L));
        verify(weatherTypeHiberRepository).findById(1L);
    }

    @Test
    public void delete_test(){
        weatherTypeHiberService.delete(1L);
        verify(weatherTypeHiberRepository).deleteById(1L);
    }

    @Test
    public void update_test(){
        when(weatherTypeHiberRepository.getRowsCount(anyString())).thenReturn(1);
        Integer rowsCount = weatherTypeHiberService.update(1L, "test");
        assertEquals(rowsCount, 1);
        verify(weatherTypeHiberRepository).getRowsCount("test");
        verify(weatherTypeHiberRepository).updateWeatherTypeById(1L, "test");
    }
}
