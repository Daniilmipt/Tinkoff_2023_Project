package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.Weather;
import org.example.services.impl.WeatherServiceImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/wheather")
@Tag(name="Контроллер для управления погодой", description="Определены Crud методы")
public class WeatherController {
    private final WeatherServiceImpl weatherService;
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public WeatherController(WeatherServiceImpl weatherService){
        this.weatherService = weatherService;
    }

    @Operation(
            summary = "Получить по температуру",
            description = "Получить по городу все температуры на заданную дату"
    )
    @GetMapping("/{regionName}")
    public HttpEntity<Object> get(@PathVariable @Parameter(description = "Название региона") String regionName,
                                  @RequestParam(value="dateTime") @Parameter(description = "Дата и время") String dateTimeRaw){
        try {
            Date dateTime = format.parse(dateTimeRaw);
            List<Integer> temeratureList = weatherService.get(regionName, dateTime);
            return new ResponseEntity<>(temeratureList, HttpStatus.OK);
        } catch (ParseException e) {
            return new ResponseEntity<>(
                    ResponseErrorBody(
                            HttpStatus.BAD_REQUEST,
                            "Incorrect dateTime format",
                            String.format("/api/wheather/%s", regionName
                            )
                    ),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Добавить новый город",
            description = "Добавить город по температуре и дате. Если такой есть - ничего не делать"
    )
    @PostMapping("/{regionName}")
    public HttpEntity<Object> add(@PathVariable @Parameter(description = "Название региона") String regionName,
                       @RequestParam(value="dateTime") @Parameter(description = "Дата и время") String dateTimeRaw,
                       @RequestParam(value="temperature") @Parameter(description = "Температура") String temperatureRaw){
        try {
            Date dateTime = format.parse(dateTimeRaw);
            Integer temperature = Integer.parseInt(temperatureRaw);
            Optional<Weather> weather = weatherService.add(regionName, temperature, dateTime);
            if (weather.isEmpty()) {
                return new ResponseEntity<>(ResponseErrorBody(
                        HttpStatus.NOT_FOUND,
                        "This object was added",
                        String.format("/api/wheather/%s", regionName
                        )
                ),HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(weather, HttpStatus.OK);
        }
        catch (NumberFormatException | ParseException exception){
            String error = exception instanceof ParseException ? "Incorrect dateTime format":  "temperature must be integer";
            return new ResponseEntity<>(ResponseErrorBody(
                    HttpStatus.BAD_REQUEST,
                    error,
                    String.format("/api/wheather/%s", regionName
                    )
            ),HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Обновить погоду",
            description = "Обновить температуру на заданную дату по городу, если такой нет, то добавить запись"
    )
    @PutMapping("/{regionName}")
    public HttpEntity<Object> put(@PathVariable @Parameter(description = "Название региона") String regionName,
                             @RequestParam(value="dateTime") @Parameter(description = "Дата и время") String dateTimeRaw,
                             @RequestParam(value="temperature") @Parameter(description = "Температура") String temperatureRaw){
        try {
            Date dateTime = format.parse(dateTimeRaw);
            Integer temperature = Integer.parseInt(temperatureRaw);
            List<Weather> weatherList = weatherService.update(regionName, temperature, dateTime);
            return new ResponseEntity<>(weatherList, HttpStatus.OK);
        }
        catch (NumberFormatException | ParseException exception){
            String error = exception instanceof ParseException ? "Incorrect dateTime format":  "temperature must be integer";
            return new ResponseEntity<>(ResponseErrorBody(
                    HttpStatus.BAD_REQUEST,
                    error,
                    String.format("/api/wheather/%s", regionName
                    )
            ),HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Удалить город",
            description = "Удалять все записи, где есть город"
    )
    @DeleteMapping("/{regionName}")
    public HttpEntity<Object> delete(@PathVariable @Parameter(description = "Название региона") String regionName){
        List<Weather> weatherList = weatherService.delete(regionName).orElse(null);
        if (weatherList == null){
            return new ResponseEntity<>(ResponseErrorBody(
                    HttpStatus.NOT_FOUND,
                    "There was not objects with this regionName",
                    String.format("/api/wheather/%s", regionName
                    )
            ),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(weatherList, HttpStatus.OK);
    }

    private static Map<String, Object> ResponseErrorBody(HttpStatus httpStatus,
                                                  String error,
                                                  String path){
        Map<String, Object> mapBody = new HashMap<>();
        mapBody.put("timestamp", new Date());
        mapBody.put("status", httpStatus.value());
        mapBody.put("error", error);
        mapBody.put("path", path);
        return mapBody;
    }
}
