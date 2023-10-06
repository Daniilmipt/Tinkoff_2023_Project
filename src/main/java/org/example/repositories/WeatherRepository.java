package org.example.repositories;

import org.example.Weather;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Repository
public class WeatherRepository {
    private final List<Weather> weatherList = createListWeather();

    public List<Integer> getTemperature(String regionName, LocalDate date){
        return weatherList.stream()
                .filter(weather
                        -> weather.getDate().equals(date)
                        && weather.getRegionName().equals(regionName))
                .map(Weather::getTemperature)
                .toList();
    }

    public Optional<Weather> addRegion(String regionName, Integer temperature, LocalDate date){
        boolean filterFlag = weatherList.stream()
                .anyMatch(weather
                        -> weather.getDate().equals(date)
                        && weather.getRegionName().equals(regionName)
                        && weather.getTemperature().equals(temperature));
        if (filterFlag){
            return Optional.empty();
        }
        Weather weather = createWeather(
                (long) Objects.hash(regionName, temperature, date),
                regionName,
                date,
                temperature
        );
        weatherList.add(weather);
        return Optional.of(weather);
    }

    public Integer updateWeather(String regionName, Integer temperature, LocalDate date){
        Predicate<Weather> filter = weather ->
                weather.getRegionName().equals(regionName)
                        && weather.getDate().equals(date);

        List<Weather> weatherFilteredList = weatherList
                .stream()
                .filter(filter)
                .toList();

        if (weatherFilteredList.isEmpty()) {
            this.addRegion(regionName, temperature, date);
            return 1;
        } else {
            weatherFilteredList
                    .forEach(weather -> weather.setTemperature(temperature));
            return weatherFilteredList.size();
        }
    }

    public void deleteWeather(String regionName){
        weatherList.removeIf(weather -> weather.getRegionName().equals(regionName));
    }

    private static List<Weather> createListWeather(){
        LocalDate dateTime = LocalDate.of(2014, Month.JANUARY, 1);
        LocalDate dateTimeSecond = LocalDate.of(2016, Month.JANUARY, 1);
        List<Weather> weatherList = new ArrayList<>(8);
        weatherList.add(createWeather(1L, "Moscow", dateTime, 14));
        weatherList.add(createWeather(2L, "Cheboksary", dateTime, 15));
        weatherList.add(createWeather(3L, "Saint Petersburg", dateTime, 26));
        weatherList.add(createWeather(4L, "Dolgoprudny", dateTime, -10));
        weatherList.add(createWeather(5L, "Dolgoprudny", dateTimeSecond, 0));
        weatherList.add(createWeather(6L, "Cheboksary", dateTime, 0));
        weatherList.add(createWeather(7L, "Chimki", dateTime, -2));
        weatherList.add(createWeather(8L, "Domodedovo", dateTime, 26));
        return weatherList;
    }

    private static Weather createWeather(Long id,
                                         String regionName,
                                         LocalDate date,
                                         Integer temperature){
        Weather weather = new Weather();
        weather.setId(id);
        weather.setRegionName(regionName);
        weather.setTemperature(temperature);
        try {
            weather.setDate(date);
        }
        catch (Exception ignored){
            weather.setDate(LocalDate.now());
        }
        return weather;
    }
}
