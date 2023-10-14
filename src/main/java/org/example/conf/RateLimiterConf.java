package org.example.conf;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConf {

    @Bean
    public RateLimiter rateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(1_000_000)
                .limitRefreshPeriod(Duration.ofDays(30))
                .timeoutDuration(Duration.ofSeconds(5))
                .build();

        return RateLimiter.of("myRateLimiter", config);
    }
}