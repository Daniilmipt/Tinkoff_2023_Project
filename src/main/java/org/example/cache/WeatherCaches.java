package org.example.cache;

import lombok.Getter;
import lombok.Setter;
import org.example.exceptions.CacheException;
import org.example.model.WeatherNew;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WeatherCaches {

    @Setter
    @Getter
    public static class WeatherObject {
        private WeatherNew weatherNew;
        private LocalDateTime localDateTime;
        private int hash;

        public WeatherObject(WeatherNew weatherNew, LocalDateTime localDateTime, Integer hash){
            this.weatherNew = weatherNew;
            this.localDateTime = localDateTime;
            this.hash = hash;
        }

        public WeatherObject(WeatherNew weatherNew, LocalDateTime localDateTime){
            this.weatherNew = weatherNew;
            this.localDateTime = localDateTime;
            this.hash = Objects.hash(weatherNew);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WeatherObject that)) return false;
            return Objects.equals(weatherNew, that.weatherNew);
        }

        @Override
        public int hashCode() {
            return Objects.hash(weatherNew);
        }
    }

    private static volatile WeatherCaches instance;

    @Getter
    private Map<String, ThreadSafeLinkedList.Node<WeatherObject>> mapCache = new ConcurrentHashMap<>();

    @Getter
    private ThreadSafeLinkedList<WeatherObject> cache = new ThreadSafeLinkedList<>();

    @Value("${cache.course.size}")
    public Long size;

    @Value("${cache.time.refresh}")
    public Long duration;

    public static WeatherCaches getInstance() {
        if (instance == null) {
            synchronized (WeatherCaches.class) {
                if (instance == null) {
                    instance = new WeatherCaches();
                }
            }
        }
        return instance;
    }

    public void clearCache(){
        mapCache = new ConcurrentHashMap<>();
        cache = new ThreadSafeLinkedList<>();
    }

    public WeatherObject getWeatherObject(String regionName){
        if (!ifExist(regionName)){
            throw new CacheException("Incorrect key cache");
        }
        return mapCache.get(regionName).getData();
    }


    public void setWeather(String regionName, WeatherNew weatherNew){
        if (!ifExist(regionName)){
            checkSize();
            ThreadSafeLinkedList.Node<WeatherObject> node =
                    cache.addFirst(new WeatherObject(weatherNew, LocalDateTime.now()));
            mapCache.put(regionName, node);
        }
    }

    public boolean ifExist(String regionName){
        checkUpdate();
        return mapCache.containsKey(regionName);
    }

    private void checkSize(){
        ThreadSafeLinkedList.Node<WeatherObject> tailNode = cache.getTail();
        while (cache.getSize() >= size){
            mapCache.entrySet().removeIf(
                    entry ->
                            entry.getValue().equals(tailNode)
            );
            cache.removeLast();
        }
    }

    private void checkUpdate(){
        LocalDateTime now = LocalDateTime.now();
        List<String> keyNames = new ArrayList<>();

        for (var keyValue: mapCache.entrySet()){
            if (Duration.between(
                            keyValue.getValue().getData().getLocalDateTime(),
                            now
                    ).getSeconds() >= duration) {
                keyNames.add(keyValue.getKey());
                cache.remove(keyValue.getValue().getData());
            }
        }

        for (String name : keyNames){
            mapCache.remove(name);
        }
    }

    public void clearWeather(WeatherNew weatherNew){
        WeatherObject weatherObject = new WeatherObject(weatherNew, LocalDateTime.now(), Objects.hashCode(weatherNew));
        cache.remove(weatherObject);
        mapCache.entrySet().removeIf(
                entry -> entry.getValue().getData().equals(weatherObject)
        );
    }
}
