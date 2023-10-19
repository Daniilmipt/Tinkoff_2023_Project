package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.Region;
import org.example.model.WeatherType;
import org.example.services.impl.Hiber.WeatherNewHiberServiceImpl;
import org.example.services.impl.JDBC.WeatherNewJdbcServiceImpl;
import org.example.services.impl.WeatherClientServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@Validated
@Tag(name="Контроллер для управления внешней погодой", description="Определены Crud методы")
public class WeatherApiDataBaseController {

    private final WeatherNewHiberServiceImpl weatherNewHiberService;
    private final WeatherNewJdbcServiceImpl weatherNewJdbcService;
    private final WeatherClientServiceImpl weatherApiService;

    public WeatherApiDataBaseController(WeatherNewHiberServiceImpl weatherNewHiberService,
                                        WeatherNewJdbcServiceImpl weatherNewJdbcService, WeatherClientServiceImpl weatherApiService) {
        this.weatherNewHiberService = weatherNewHiberService;
        this.weatherNewJdbcService = weatherNewJdbcService;
        this.weatherApiService = weatherApiService;
    }

    @Operation(
            summary = "Получить сегодняшнюю температуру по городу",
            description = "Получить температуру в цельсиях на сегодняшнюю дату по заданному городу"
    )
    @GetMapping("/external/hibernate")
    public void externalCurrentTemperature(@RequestParam(value="q", required = false)
                                                             @Parameter(description = "q")
                                                             String q){
        Integer temperature = weatherApiService.getCurrentTemperature(q).asInt();
        weatherNewHiberService.saveByWeatherTypeAndRegion(new WeatherType("Warm"), new Region(q), temperature);
    }

    @Operation(
            summary = "Получить сегодняшнюю температуру по городу",
            description = "Получить температуру в цельсиях на сегодняшнюю дату по заданному городу"
    )
    @GetMapping("/external/jdbc")
    public void externalJdbcCurrentTemperature(@RequestParam(value="q", required = false)
                                           @Parameter(description = "q")
                                           String q) throws SQLException {
        Integer temperature = weatherApiService.getCurrentTemperature(q).asInt();
        weatherNewJdbcService.saveByWeatherTypeAndRegion(new WeatherType("Warm"), new Region(q), temperature);
    }
}
