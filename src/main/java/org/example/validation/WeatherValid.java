package org.example.validation;

import org.example.Weather;
import org.example.dto.ResponseErrorDto;
import org.example.exceptions.enums.OperationException;
import org.example.exceptions.enums.WeatherValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class WeatherValid{
    private final String dateRaw;
    private final String temperatureRaw;
    private final String path;
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public WeatherValid(String dateRaw, String temperatureRaw, String path){
        this.dateRaw = dateRaw;
        this.temperatureRaw = temperatureRaw;
        this.path = path;
    }

    public ResponseEntity<Object> checkParams(){
        ResponseEntity<Object> nullValid = checkNullParams();
        return nullValid == null ? checkValidParams() : nullValid;
    }

    public ResponseEntity<Object> checkNullParams(){
        if (dateRaw == null || temperatureRaw == null){
            WeatherValidException errorEnum =
                    dateRaw == null ?
                            WeatherValidException.DateTime_NULL :
                            WeatherValidException.TEMPERATURE_NULL;
            ResponseErrorDto responseErrorDto = new ResponseErrorDto(
                    errorEnum.getMessage(),
                    LocalDate.now(),
                    path
            );
            return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    public ResponseEntity<Object> checkValidParams(){
        ResponseEntity<Object> dateValid = checkValidDate();
        return dateValid == null ? checkValidTemperature() : dateValid;
    }

    public ResponseEntity<Object> checkValidDate(){
        try {
            LocalDate ignored = LocalDate.parse(dateRaw, formatter);
        }catch (DateTimeParseException ignored){
            ResponseErrorDto responseErrorDto = new ResponseErrorDto(
                    WeatherValidException.INCORRECT_DateTime.getMessage(),
                    LocalDate.now(),
                    path
            );
            return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    public ResponseEntity<Object> checkValidTemperature(){
        try {
            Integer ignored = Integer.parseInt(temperatureRaw);
        }
        catch (NumberFormatException ignored){
            ResponseErrorDto responseErrorDto = new ResponseErrorDto(
                    WeatherValidException.INCORRECT_TEMPERATURE.getMessage(),
                    LocalDate.now(),
                    path
            );
            return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    public ResponseEntity<Object> addValid(Optional<Weather> weather){
        if (weather.isEmpty()) {
            ResponseErrorDto responseErrorDto = new ResponseErrorDto(
                    OperationException.WEATHER_ADD_AGAIN.getMessage(),
                    LocalDate.now(),
                    path
            );
            return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(weather.get(), HttpStatus.CREATED);
    }
}
