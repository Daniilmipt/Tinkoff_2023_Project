package org.example.repositories;

import org.example.Weather;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

@Repository
public class WeatherRepository {
    private final List<Weather> weatherList = createListWeather();
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public List<Integer> getTemperature(String regionName, Date dateTime){
        return weatherList.stream()
                .filter(weather
                        -> weather.getDateTime().equals(dateTime)
                        && weather.getRegionName().equals(regionName))
                .map(Weather::getTemperature)
                .toList();
    }

    public Optional<Weather> addRegion(String regionName, Integer temperature, Date dateTime){
        List<Weather> weatherListFilter = weatherList.stream()
                .filter(weather
                        -> weather.getDateTime().equals(dateTime)
                        && weather.getRegionName().equals(regionName)
                        && weather.getTemperature().equals(temperature))
                .toList();
        if (!weatherListFilter.isEmpty()){
            return Optional.empty();
        }
        Weather weather = new Weather();
        weather.setRegionName(regionName);
        weather.setTemperature(temperature);
        weather.setDateTime(dateTime);
        weather.setId((long) Objects.hash(regionName, temperature, dateTime));
        weatherList.add(weather);
        return Optional.of(weather);
    }

    public List<Weather> updateWeather(String regionName, Integer temperature, Date dateTime){
        Predicate<Weather> filter = weather ->
                weather.getRegionName().equals(regionName)
                        && weather.getDateTime().equals(dateTime);

        List<Weather> weatherFilteredList = weatherList
                .stream()
                .filter(filter)
                .toList();

        if (!weatherFilteredList.isEmpty()) {
            weatherFilteredList
                    .forEach(weather -> weather.setTemperature(temperature));
            return weatherFilteredList;
        } else {
            Optional<Weather> weather = this.addRegion(regionName, temperature, dateTime);
            return new ArrayList<>(List.of(weather.orElse(new Weather())));
        }
    }

    public Optional<List<Weather>> deleteWeather(String regionName){
        Predicate<Weather> filter = weather -> weather.getRegionName().equals(regionName);
        List<Weather> weatherListDeleted = weatherList
                .stream()
                .filter(filter)
                .toList();
        if (weatherListDeleted.isEmpty()){
            return Optional.empty();
        }
        weatherList.removeIf(filter);
        return Optional.of(weatherListDeleted);
    }

    private static List<Weather> createListWeather(){
        List<Weather> weatherList = new ArrayList<>(8);
        weatherList.add(createWeather(1L, "Moscow", "2023/09/01 20:01:02", 14));
        weatherList.add(createWeather(2L, "Cheboksary", "2023/09/01 20:01:02", 15));
        weatherList.add(createWeather(3L, "Saint Petersburg", "2023/09/01 20:01:02", 26));
        weatherList.add(createWeather(4L, "Dolgoprudny", "2023/09/01 20:01:02", -10));
        weatherList.add(createWeather(5L, "Dolgoprudny", "2023/09/01 20:01:02", 0));
        weatherList.add(createWeather(6L, "Cheboksary", "2023/09/01 20:01:02", 0));
        weatherList.add(createWeather(7L, "Chimki", "2023/09/01 20:01:02", -2));
        weatherList.add(createWeather(8L, "Domodedovo", "2023/09/01 20:01:02", 26));
        return weatherList;
    }

    private static Weather createWeather(Long id,
                                         String regionName,
                                         String dateTimeString,
                                         Integer temperature){
        Weather weather = new Weather();
        weather.setId(id);
        weather.setRegionName(regionName);
        weather.setTemperature(temperature);
        try {
            weather.setDateTime(format.parse(dateTimeString));
        }
        catch (Exception ignored){
            weather.setDateTime(new Date());
        }
        return weather;
    }
}
