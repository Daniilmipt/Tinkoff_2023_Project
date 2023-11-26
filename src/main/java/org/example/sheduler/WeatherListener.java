package org.example.sheduler;

import org.example.model.Region;
import org.example.services.impl.Hiber.RegionHiberServiceImpl;
import org.example.services.impl.Hiber.WeatherNewHiberServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Component
public class WeatherListener {

    @Value("${kafka.windows-size}")
    private int WINDOW_SIZE;
    private static final Logger logger = LoggerFactory.getLogger(WeatherListener.class);


    private final WeatherNewHiberServiceImpl weatherNewHiberService;
    private final RegionHiberServiceImpl regionHiberService;

    public WeatherListener(WeatherNewHiberServiceImpl weatherNewHiberService, RegionHiberServiceImpl regionHiberService) {
        this.weatherNewHiberService = weatherNewHiberService;
        this.regionHiberService = regionHiberService;
    }

    @KafkaListener(topics = "fintech_tinkoff", groupId = "your-group-id")
    public void listen(String message) {
        Optional<Region> region = regionHiberService.getByName(message);
        if(region.isPresent()){
            Pageable pageable = PageRequest.of(0, WINDOW_SIZE);
            List<Integer> temperatures = weatherNewHiberService.getLastWeathersByRegion(region.get().getId(), pageable);
            logger.info("Город: " + message + "; Температура: " + meanTemperature(temperatures));
        }
    }

    private Double meanTemperature(List<Integer> temperatures){
        return temperatures
                .stream()
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);
    }
}

