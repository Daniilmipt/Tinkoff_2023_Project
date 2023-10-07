package org.example.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component("WebClientApiWeather")
public class WebClientConfig {
    @Value("${weather.api.base-url}")
    String baseUrl;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().baseUrl(baseUrl);
    }
}