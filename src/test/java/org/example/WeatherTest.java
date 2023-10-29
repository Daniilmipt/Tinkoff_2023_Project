package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherTest {
    Weather weather;
    static List<Weather> weatherList;

    @BeforeEach
    void createWeather(){
        weather = new Weather();
    }

    @BeforeAll
    static void createListWeather(){
        weatherList = new ArrayList<>(8);
        weatherList.add(createWeather(1L, "Moscow", LocalDate.now(), 14));
        weatherList.add(createWeather(2L, "Cheboksary", LocalDate.now(), 15));
        weatherList.add(createWeather(3L, "Saint Petersburg", LocalDate.now(), 26));
        weatherList.add(createWeather(4L, "Dolgoprudny", LocalDate.now(), -10));
        weatherList.add(createWeather(5L, "Dolgoprudny", LocalDate.now(), 0));
        weatherList.add(createWeather(6L, "Cheboksary", LocalDate.now(), 0));
        weatherList.add(createWeather(7L, "Chimki", LocalDate.now(), -2));
        weatherList.add(createWeather(8L, "Domodedovo", LocalDate.now(), 26));
    }

    static Weather createWeather(Long id, String regionName, LocalDate date, Integer temperature){
        Weather weather = new Weather();
        weather.setId(id);
        weather.setRegionName(regionName);
        weather.setTemperature(temperature);
        weather.setDate(date);
        return weather;
    }

    @Test
    void getAndSet_Id() {
        System.out.println(weather);
        weather.setId(1L);
        assertEquals(1L, weather.getId());
    }

    @Test
    void getAndSet_RegionName() {
        weather.setRegionName("Moscow");
        assertEquals("Moscow", weather.getRegionName());
    }

    @Test
    void getAndSet_Temperature() {
        weather.setTemperature(24);
        assertEquals(24, weather.getTemperature());
    }

//    @Test
//    void getAndSet_DateTime() {
//        String stringDate = "Thu, Dec 31 1998 23:37:50";
//        try {
//            Date testDate = new SimpleDateFormat("E, MMM dd yyyy HH:mm:ss").parse(stringDate);
//            weather.setDate(testDate);
//            assertEquals(testDate, weather.getDateTime());
//        } catch (ParseException ignored) {
//        }
//    }

    @Test
    void average_Temperature_ByRegion() {
        double averageTemperature = weatherList.stream()
                .mapToDouble(Weather::getTemperature)
                .average()
                .orElse(Double.MAX_VALUE);
        assertEquals(8.625, averageTemperature, 0.0001);
    }

    @Test
    void filter_Region_ByMoreTemperature() {
        Integer testTemperature = 14;
        List<String> regionTestList = new ArrayList<>(List.of("Cheboksary", "Saint Petersburg", "Domodedovo"));

        List<String> regionActualList = weatherList.stream()
                .filter(weather -> weather.getTemperature() > testTemperature)
                .map(Weather::getRegionName)
                .distinct()
                .toList();
        assertEquals(regionTestList, regionActualList);
    }

    @Test
    void collectMap_ByID_ByTemperature() {
        Map<Long, List<Integer>> weatherTestMap = new HashMap<>(8);
        weatherTestMap.put(1L, new ArrayList<>(List.of(14)));
        weatherTestMap.put(2L, new ArrayList<>(List.of(15)));
        weatherTestMap.put(3L, new ArrayList<>(List.of(26)));
        weatherTestMap.put(4L, new ArrayList<>(List.of(-10)));
        weatherTestMap.put(5L, new ArrayList<>(List.of(0)));
        weatherTestMap.put(6L, new ArrayList<>(List.of(0)));
        weatherTestMap.put(7L, new ArrayList<>(List.of(-2)));
        weatherTestMap.put(8L, new ArrayList<>(List.of(26)));

        Map<Long, List<Integer>> weatherActualMap = weatherList.stream()
                .collect(Collectors.groupingBy(
                        Weather::getId,
                        Collectors.mapping(Weather::getTemperature, Collectors.toList())
                ));
        assertEquals(weatherTestMap, weatherActualMap);
    }

    @Test
    void collectMap_ByTemperature_ByWeather() {
        Map<Integer, List<Weather>> weatherTestMap = new HashMap<>(8);
        weatherTestMap.put(0, new ArrayList<>(List.of(weatherList.get(4), weatherList.get(5))));
        weatherTestMap.put(-2, new ArrayList<>(List.of(weatherList.get(6))));
        weatherTestMap.put(-10, new ArrayList<>(List.of(weatherList.get(3))));
        weatherTestMap.put(14, new ArrayList<>(List.of(weatherList.get(0))));
        weatherTestMap.put(26, new ArrayList<>(List.of(weatherList.get(2), weatherList.get(7))));
        weatherTestMap.put(15, new ArrayList<>(List.of(weatherList.get(1))));

        Map<Integer, List<Weather>> weatherActualMap = weatherList.stream()
                .collect(Collectors.groupingBy(
                        Weather::getTemperature,
                        Collectors.mapping(weather -> weather, Collectors.toList())
                ));
        assertEquals(weatherTestMap, weatherActualMap);
    }
}