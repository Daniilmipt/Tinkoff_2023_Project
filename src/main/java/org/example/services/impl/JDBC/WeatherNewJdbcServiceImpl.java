package org.example.services.impl.JDBC;

import org.example.enums.jdbc.WeatherSql;
import org.example.exceptions.SqlException;
import org.example.exceptions.weatherApi.ResponseException;
import org.example.model.Region;
import org.example.model.WeatherNew;
import org.example.model.WeatherType;
import org.example.services.WeatherNewService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class WeatherNewJdbcServiceImpl implements WeatherNewService {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public WeatherNewJdbcServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public WeatherNew save(WeatherNew weatherNew) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            return insertRow(jdbcTemplate, weatherNew);
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
            throw new SqlException(message, "table weather", 500);
        }
    }


    @Override
    public WeatherNew saveByWeatherTypeAndRegion(WeatherType weatherType, Region region, Integer temperature) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Savepoint save = connection.setSavepoint();
            try {
                Region regionInserted = RegionJdbcServiceImpl.insertRow(jdbcTemplate, region);
                Savepoint saveRegion = connection.setSavepoint();
                try {
                    WeatherType weatherTypeInserted = WeatherTypeJdbcServiceImpl.insertRow(jdbcTemplate, weatherType);
                    Savepoint saveRegionAndType = connection.setSavepoint();
                    try {
                        WeatherNew weatherNew = new WeatherNew(
                                regionInserted.getId(),
                                weatherTypeInserted.getId(),
                                temperature,
                                LocalDate.now()
                        );
                        WeatherNew weatherNewInserted = insertRow(jdbcTemplate, weatherNew);
                        connection.commit();
                        return weatherNewInserted;
                    } catch (Exception e) {
                        connection.rollback(saveRegionAndType);
                        throw new SqlException(e.getMessage(), "database exception", 500);
                    }
                }
                catch (Exception e) {
                    if (!(e instanceof ResponseException)){
                        connection.rollback(saveRegion);
                    }
                    throw e;
                }
            } catch (Exception e) {
                if (!(e instanceof ResponseException)){
                    connection.rollback(save);
                }
                throw e;
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
            throw new SqlException(message, "table weather", 500);
        }
    }

    @Override
    public Optional<WeatherNew> getByRegionAndDate(Long region_id, LocalDate date) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            try {
                return Optional.ofNullable(jdbcTemplate.queryForObject(
                        WeatherSql.SELECT_BY_REGION_DATE.getMessage(), new Object[]{region_id, Date.valueOf(date)}, (rs, rowNum) ->
                                new WeatherNew(
                                        rs.getLong("id"),
                                        rs.getLong("region_id"),
                                        rs.getLong("type_id"),
                                        rs.getInt("temperature"),
                                        rs.getDate("date").toLocalDate()
                                )
                ));
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
                throw new SqlException(message, "table weather", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
            throw new SqlException(message, "table weather", 500);
        }
    }

    @Override
    public Optional<WeatherNew> get(Long weatherModel_id) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            try {
                return Optional.ofNullable(jdbcTemplate.queryForObject(
                        WeatherSql.SELECT.getMessage(), new Object[]{weatherModel_id}, (rs, rowNum) ->
                                new WeatherNew(
                                        rs.getLong("id"),
                                        rs.getLong("region_id"),
                                        rs.getLong("type_id"),
                                        rs.getInt("temperature"),
                                        rs.getDate("date").toLocalDate()
                                )
                ));
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
                throw new SqlException(message, "table weather", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
            throw new SqlException(message, "table weather", 500);
        }
    }

    @Override
    public void deleteByRegion(Long regionId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try {
                jdbcTemplate.update(
                        WeatherSql.DELETE_BY_REGION.getMessage(),
                        regionId
                );
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
                throw new SqlException(message, "table weather", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
            throw new SqlException(message, "table weather", 500);
        }
    }

    @Override
    public void deleteByRegionAndDate(Long regionId, LocalDate date) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try {
                jdbcTemplate.update(
                        WeatherSql.DELETE_BY_REGION_DATE.getMessage(),
                        regionId,
                        Date.valueOf(date)
                );
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
                throw new SqlException(message, "table weather", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
            throw new SqlException(message, "table weather", 500);
        }
    }

    @Override
    public void updateTemperatureByRegionAndDate(Long region_id, Integer temperature, LocalDate date) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try {
                jdbcTemplate.update(
                        WeatherSql.UPDATE_TEMPERATURE.getMessage(),
                        temperature,
                        region_id,
                        Date.valueOf(date)
                );
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
                throw new SqlException(message, "table weather", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
            throw new SqlException(message, "table weather", 500);
        }
    }

    @Override
    public void updateTypeByRegionAndDate(Long region_id, Long type_id, LocalDate date) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try {
                jdbcTemplate.update(
                        WeatherSql.UPDATE_WEATHER.getMessage(),
                        type_id,
                        region_id,
                        Date.valueOf(date)
                );
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
                throw new SqlException(message, "table weather", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause() + "; " + e.getMessage();
            throw new SqlException(message, "table weather", 500);
        }
    }

    private static Optional<WeatherNew> findIfExists(JdbcTemplate jdbcTemplate, Long region_id, LocalDate date) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    WeatherSql.SELECT_IF_EXISTS.getMessage(), new Object[]{region_id, Date.valueOf(date)}, (rs, rowNum) ->
                            new WeatherNew(
                                    rs.getLong("id"),
                                    rs.getLong("region_id"),
                                    rs.getLong("type_id"),
                                    rs.getInt("temperature"),
                                    rs.getDate("date").toLocalDate()
                            )
            ));
        } catch (DataAccessException e){
            return Optional.empty();
        }
    }

    public static WeatherNew insertRow(JdbcTemplate jdbcTemplate, WeatherNew weatherNew) {
        try {
            jdbcTemplate.update(
                    WeatherSql.INSERT.getMessage(),
                    weatherNew.getRegion_id(),
                    weatherNew.getType_id(),
                    weatherNew.getTemperature(),
                    Date.valueOf(weatherNew.getDate())
            );
            return findIfExists(jdbcTemplate, weatherNew.getRegion_id(), weatherNew.getDate()).get();
        } catch (DataAccessException e){
            return findIfExists(jdbcTemplate, weatherNew.getRegion_id(), weatherNew.getDate()).get();
        }
    }
}
