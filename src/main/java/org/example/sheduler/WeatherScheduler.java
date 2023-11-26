package org.example.sheduler;

import org.example.cache.WeatherCaches;
import org.example.model.Region;
import org.example.model.WeatherType;
import org.example.services.impl.Hiber.WeatherNewHiberServiceImpl;
import org.example.services.impl.WeatherClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

@Component
public class WeatherScheduler {

    private final WeatherNewHiberServiceImpl weatherNewHiberService;
    private final WeatherClientServiceImpl weatherApiService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic-name}")
    private String kafkaTopic;

    @Autowired
    public WeatherScheduler(WeatherNewHiberServiceImpl weatherNewHiberService, WeatherClientServiceImpl weatherApiService, KafkaTemplate<String, String> kafkaTemplate) {
        this.weatherNewHiberService = weatherNewHiberService;
        this.weatherApiService = weatherApiService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(cron = "0/10 * * * * *") // Каждые 10 секунд
    public void requestMoscowWeather() {
        getAndSaveWeather("Moscow");
        sendMessageToKafka("Moscow");
    }

    @Scheduled(initialDelay = 5000, cron = "0/20 * * * * *") // Каждые 20 секунд
    public void requestLondonWeather() {
        getAndSaveWeather("London");
        sendMessageToKafka("London");
    }

    @Scheduled(initialDelay = 10000, cron = "0/30 * * * * *")
    public void requestSamaraWeather() {
        getAndSaveWeather("Samara");
        sendMessageToKafka("Samara");
    }

    private void sendMessageToKafka(String city) {
        kafkaTemplate.send(kafkaTopic, city);
    }

    private void getAndSaveWeather(String region){
        Integer temperature = weatherApiService.getCurrentTemperature(region).asInt();
        weatherNewHiberService.saveByWeatherTypeAndRegion(
                new WeatherType("Warm"),
                new Region(region),
                temperature
        );

    }
}

