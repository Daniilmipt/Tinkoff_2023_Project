package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.Weather;
import org.example.services.impl.WeatherApiServiceImpl;
import org.example.services.impl.WeatherServiceImpl;
import org.example.validation.WeatherValid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/wheather")
@Tag(name="Контроллер для управления погодой", description="Определены Crud методы")
public class WeatherController {
    private final WeatherServiceImpl weatherService;
    private final WeatherApiServiceImpl weatherApiService;
    public WeatherController(WeatherServiceImpl weatherService, WeatherApiServiceImpl weatherApiService){
        this.weatherService = weatherService;
        this.weatherApiService = weatherApiService;
    }

    @Operation(
            summary = "Получить сегодняшнюю температуру по городу",
            description = "Получить температуру в цельсиях на сегодняшнюю дату по заданному городу"
    )
    @GetMapping("/external")
    public ResponseEntity<Object> externalCurrentTemperature(@RequestParam(value="q", required = false)
                                           @Parameter(description = "q")
                                            String q){
        return weatherApiService.getCurrentTemperature(q);
    }

    @Operation(
            summary = "Получить по температуру",
            description = "Получить по городу все температуры на заданную дату"
    )
    @GetMapping("/{regionName}")
    public ResponseEntity<Object> get(@PathVariable
                                      @Parameter(description = "Название региона")
                                          String regionName,
                                      @RequestParam(value="dateTime", required = false)
                                      @Parameter(description = "Дата и время")
                                      String dateRaw){
        WeatherValid weatherValid = new WeatherValid(
                dateRaw,
                "0",
                String.format("/api/wheather/%s", regionName)
        );
        ResponseEntity<Object> validAnswer = weatherValid.checkParams();
        if (validAnswer == null) {
            List<Integer> temeratureList = weatherService.get(
                    regionName,
                    LocalDate.parse(dateRaw, WeatherValid.formatter)
            );
            return new ResponseEntity<>(temeratureList, HttpStatus.OK);
        }
        return validAnswer;
    }

    @Operation(
            summary = "Добавить новый город",
            description = "Добавить город по температуре и дате. Если такой есть - ничего не делать"
    )
    @PostMapping("/{regionName}")
    public ResponseEntity<Object> add(@PathVariable @Parameter(description = "Название региона") String regionName,
                                      @RequestParam(value="dateTime", required = false)
                                      @Parameter(description = "Дата и время")
                                      String dateRaw,
                                      @RequestParam(value="temperature", required = false)
                                      @Parameter(description = "Температура")
                                      String temperatureRaw){
        WeatherValid weatherValid = new WeatherValid(
                dateRaw,
                temperatureRaw,
                String.format("/api/wheather/%s", regionName)
        );
        ResponseEntity<Object> validAnswer = weatherValid.checkParams();
        if (validAnswer == null) {
            Optional<Weather> weather = weatherService.add(
                    regionName,
                    Integer.parseInt(temperatureRaw),
                    LocalDate.parse(dateRaw, WeatherValid.formatter)
            );
            return weatherValid.addValid(weather);
        }
        return validAnswer;
    }

    @Operation(
            summary = "Обновить погоду",
            description = "Обновить температуру на заданную дату по городу, если такой нет, то добавить запись"
    )
    @PutMapping("/{regionName}")
    public ResponseEntity<Object> put(@PathVariable @Parameter(description = "Название региона") String regionName,
                                      @RequestParam(value="dateTime", required = false)
                                      @Parameter(description = "Дата и время")
                                      String dateRaw,
                                      @RequestParam(value="temperature", required = false)
                                      @Parameter(description = "Температура")
                                      String temperatureRaw){
        WeatherValid weatherValid = new WeatherValid(
                dateRaw,
                temperatureRaw,
                String.format("/api/wheather/%s", regionName)
        );
        ResponseEntity<Object> validAnswer = weatherValid.checkParams();
        if (validAnswer == null) {
            Integer count = weatherService.updateTempetatureByRegionAndDatetime(
                    regionName,
                    Integer.parseInt(temperatureRaw),
                    LocalDate.parse(dateRaw, WeatherValid.formatter)
            );
            return new ResponseEntity<>(count, HttpStatus.OK);
        }
        return validAnswer;
    }

    @Operation(
            summary = "Удалить город",
            description = "Удалять все записи, где есть город"
    )
    @DeleteMapping("/{regionName}")
    public ResponseEntity<Object> delete(@PathVariable
                                         @Parameter(description = "Название региона")
                                         String regionName){
        weatherService.deleteWeatherByRegionName(regionName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
