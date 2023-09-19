package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static List<Weather> weatherList;

    public static void main(String[] args) {
        createListWeather();
        System.out.println("Average temperature for regions = " + averageTemperatureByRegion());
        System.out.println();

        Integer testTemperature = 14;
        System.out.println("Regions with temperature less than "+testTemperature+": "+filterRegionByMoreTemperature(testTemperature));
        System.out.println();

        System.out.println("Convert list to map with {id, [temperature]}: " + collectMapByIDByTemperature());
        System.out.println();

        System.out.println("Convert list to map with {temperature, [Weather]}: " + collectMapByTemperatureByWeather());
        System.out.println();
    }

    public static void createListWeather(){
        weatherList = new ArrayList<>(8);
        weatherList.add(createWeather(1L, "Moscow", new Date(), 14));
        weatherList.add(createWeather(2L, "Cheboksary", new Date(), 15));
        weatherList.add(createWeather(3L, "Saint Petersburg", new Date(), 26));
        weatherList.add(createWeather(4L, "Dolgoprudny", new Date(), -10));
        weatherList.add(createWeather(5L, "Dolgoprudny", new Date(), 0));
        weatherList.add(createWeather(6L, "Cheboksary", new Date(), 0));
        weatherList.add(createWeather(7L, "Chimki", new Date(), -2));
        weatherList.add(createWeather(8L, "Domodedovo", new Date(), 26));
    }

    public static Weather createWeather(Long id, String regionName, Date dateTime, Integer temperature){
        Weather weather = new Weather();
        weather.setId(id);
        weather.setRegionName(regionName);
        weather.setTemperature(temperature);
        weather.setDateTime(dateTime);
        return weather;
    }

    public static double averageTemperatureByRegion() {
        return weatherList.stream()
                .mapToDouble(Weather::getTemperature)
                .average()
                .orElse(Double.MAX_VALUE);
    }

    public static List<String> filterRegionByMoreTemperature(Integer testTemperature) {
        return weatherList.stream()
                .filter(weather1 -> weather1.getTemperature() > testTemperature)
                .map(Weather::getRegionName)
                .distinct()
                .toList();
    }

    public static Map<Long, List<Integer>> collectMapByIDByTemperature() {
        return weatherList.stream()
                .collect(Collectors.groupingBy(
                        Weather::getId,
                        Collectors.mapping(Weather::getTemperature, Collectors.toList())
                ));
    }

    public static Map<Integer, List<Weather>> collectMapByTemperatureByWeather() {
        return weatherList.stream()
                .collect(Collectors.groupingBy(
                        Weather::getTemperature,
                        Collectors.mapping(weather -> weather, Collectors.toList())
                ));
    }
}
