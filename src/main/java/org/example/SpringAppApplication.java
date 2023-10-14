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

import java.sql.SQLException;
import java.time.LocalDate;

@SpringBootApplication
public class SpringAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAppApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(RegionJdbcServiceImpl regionHiberService){
//        return args -> {
//            Region region = new Region();
//            region.setName("first");
//            System.out.println(region.getId());
//            regionHiberService.save(region);
//
//            System.out.println(regionHiberService.get(1L).get(0).getName());
//
//            regionHiberService.delete(2L);
//            regionHiberService.update(1L, "QQQ");
//        };
//    }

//    @Bean
//    public CommandLineRunner commandLineRunner1(WeatherTypeJdbcServiceImpl weatherTypeHiberService){
//        return args -> {
//            WeatherType weatherType = new WeatherType();
//            weatherType.setDescription("first");
//            weatherType.setId(4L);
//            System.out.println(weatherType.getId());
//            weatherTypeHiberService.save(weatherType);
//
//            System.out.println(weatherTypeHiberService.get(1L).get(0).getDescription());
//
//            weatherTypeHiberService.delete(2L);
//            weatherTypeHiberService.update(1L, "QQQ");
//        };
//    }


    @Bean
    public CommandLineRunner commandLineRunner2(RegionJdbcServiceImpl regionJdbcService,
                                                RegionHiberServiceImpl regionHiberService,
                                                WeatherTypeJdbcServiceImpl weatherTypeJdbcService,
                                                WeatherTypeHiberServiceImpl weatherTypeHiberService,
                                                WeatherNewJdbcServiceImpl weatherNewJdbcService){
        return args -> {
            RegionRunnerHiber(regionHiberService);
        };
    }

    private void RegionRunnerJdbc(RegionJdbcServiceImpl regionJdbcService) throws SQLException {
        Region region = new Region();
        region.setName("first");
        region.setId(100L);
        regionJdbcService.save(region);

        System.out.println(regionJdbcService.get(1L).orElse(null).getName());

        regionJdbcService.delete(2L);
        regionJdbcService.update(1L, "QQQ");
    }

    private void RegionRunnerHiber(RegionHiberServiceImpl regionJdbcService) throws SQLException {
        Region region = new Region();
        region.setName("first");
        region.setId(100L);
        regionJdbcService.save(region);

//        System.out.println(regionJdbcService.get(1L).orElse(null).getName());
//
//        regionJdbcService.delete(2L);
//        regionJdbcService.update(1L, "QQQ");
    }

    private void WeatherTypeRunnerJdbc(WeatherTypeJdbcServiceImpl weatherTypeJdbcService) throws SQLException {
        WeatherType weatherType = new WeatherType();
        weatherType.setDescription("first");
        weatherType.setId(4L);
        System.out.println(weatherType.getId());
        weatherTypeJdbcService.save(weatherType);

        System.out.println(weatherTypeJdbcService.get(1L).orElse(null).getDescription());
        System.out.println(weatherTypeJdbcService.get(100L).orElse(null));

        weatherTypeJdbcService.delete(2L);
        weatherTypeJdbcService.update(1L, "QQQ");
    }

    private void WeatherTypeRunnerHiber(WeatherTypeHiberServiceImpl weatherTypeHiberService) throws SQLException {
        WeatherType weatherType = new WeatherType();
        weatherType.setDescription("first");
        weatherType.setId(4L);
        System.out.println(weatherType.getId());
        weatherTypeHiberService.save(weatherType);

//        System.out.println(weatherTypeHiberService.get(1L).orElse(null).getDescription());
//        System.out.println(weatherTypeHiberService.get(100L).orElse(null));
//
//        weatherTypeHiberService.delete(2L);
//        weatherTypeHiberService.update(1L, "QQQ");
    }

    private void WeatherRunnerJdbc(WeatherNewJdbcServiceImpl weatherNewJdbcService) throws SQLException {
        WeatherNew weatherNew = new WeatherNew();
        weatherNew.setId(10L);
        weatherNew.setRegion_id(1L);
        weatherNew.setType_id(5L);
        weatherNew.setTemperature(10);
        weatherNew.setDate(LocalDate.now());
        weatherNewJdbcService.save(weatherNew);

        System.out.println(weatherNewJdbcService.get(1L).orElse(null).getTemperature());
        System.out.println(weatherNewJdbcService.getByRegionAndDate(1L, LocalDate.now()));
    }

}