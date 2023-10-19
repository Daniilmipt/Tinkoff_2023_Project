package org.example;

//import org.example.services.impl.RegionJDBCServiceImpl;
import org.example.model.Region;
import org.example.model.WeatherNew;
import org.example.model.WeatherType;
import org.example.services.impl.Hiber.RegionHiberServiceImpl;
import org.example.services.impl.Hiber.WeatherTypeHiberServiceImpl;
import org.example.services.impl.JDBC.RegionJdbcServiceImpl;
import org.example.services.impl.JDBC.WeatherNewJdbcServiceImpl;
import org.example.services.impl.JDBC.WeatherTypeJdbcServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;
import java.time.LocalDate;

@SpringBootApplication
public class SpringAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAppApplication.class, args);
    }
}