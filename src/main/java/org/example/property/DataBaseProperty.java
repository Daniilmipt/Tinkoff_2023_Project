package org.example.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

public class DataBaseProperty {
    @Getter
    @Value("${spring.datasource.url}")
    private static String url;

    @Getter
    @Value("${spring.datasource.driver-class-name}")
    private static String driverClassName;

    @Getter
    @Value("${spring.datasource.username}")
    private static String username;

    @Getter
    @Value("${spring.datasource.password}")
    private static String password;

}
