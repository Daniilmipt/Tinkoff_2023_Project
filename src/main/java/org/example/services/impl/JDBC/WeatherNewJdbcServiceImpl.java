package org.example.services.impl.JDBC;

import org.example.dto.RegionDto;
import org.example.dto.WeatherDto;
import org.example.dto.WeatherTypeDto;
import org.example.enums.jdbc.WeatherSql;
import org.example.exceptions.SqlException;
import org.example.exceptions.weatherApi.ResponseException;
import org.example.mapper.WeatherMapper;
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

    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Override
    public WeatherDto save(WeatherNew weatherNew) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            return insertRow(connection, weatherNew);
        }
    }


    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Override
    public WeatherDto saveByWeatherTypeAndRegion(WeatherType weatherType, Region region, Integer temperature) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Savepoint save = connection.setSavepoint();
            try {
                RegionDto regionInserted = RegionJdbcServiceImpl.insertRow(connection, region);
                Savepoint saveRegion = connection.setSavepoint();
                try {
                    WeatherTypeDto weatherTypeInserted = WeatherTypeJdbcServiceImpl.insertRow(connection, weatherType);
                    Savepoint saveRegionAndType = connection.setSavepoint();
                    try {
                        WeatherNew weatherNew = new WeatherNew(
                                regionInserted.getId(),
                                weatherTypeInserted.getId(),
                                temperature,
                                LocalDate.now()
                        );
                        WeatherDto weatherNewInserted = insertRow(connection, weatherNew);
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
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather", 500);
        }
    }

    /*
    READ_UNCOMMITTED, т.к. в таблицы нечасто вносят изменения посредством update
     */
    @Override
    public Optional<WeatherDto> getByRegionAndDate(Long region_id, LocalDate date) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            try (PreparedStatement selectStatement = connection.prepareStatement(WeatherSql.SELECT_BY_REGION_DATE.getMessage())) {
                selectStatement.setLong(1, region_id);
                selectStatement.setDate(2, Date.valueOf(date));
                ResultSet rs = selectStatement.executeQuery();
                Optional<WeatherNew> weatherNew = Optional.empty();
                if (rs.next()) {
                    weatherNew = Optional.of(new WeatherNew(
                            rs.getLong("id"),
                            rs.getLong("region_id"),
                            rs.getLong("type_id"),
                            rs.getInt("temperature"),
                            rs.getDate("date").toLocalDate()
                    ));
                }
                return WeatherMapper.optionalEntityToDto(weatherNew);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather", 500);
        }
    }

    /*
    READ_UNCOMMITTED, т.к. в таблицы нечасто вносят изменения посредством update
     */
    @Override
    public Optional<WeatherDto> get(Long weatherModel_id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

            try (PreparedStatement selectStatement = connection.prepareStatement(WeatherSql.SELECT.getMessage())) {
                selectStatement.setLong(1, weatherModel_id);
                ResultSet rs = selectStatement.executeQuery();
                Optional<WeatherNew> weatherNew = Optional.empty();
                if (rs.next()) {
                    weatherNew = Optional.of(new WeatherNew(
                            rs.getLong("id"),
                            rs.getLong("region_id"),
                            rs.getLong("type_id"),
                            rs.getInt("temperature"),
                            rs.getDate("date").toLocalDate()
                    ));
                }
                return WeatherMapper.optionalEntityToDto(weatherNew);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather", 500);
        }
    }

    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Override
    public void deleteByRegion(Long regionId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try (PreparedStatement deleteStatement = connection.prepareStatement(WeatherSql.DELETE_BY_REGION.getMessage())) {
                deleteStatement.setLong(1, regionId);
                deleteStatement.execute();
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather", 500);
        }
    }

    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Override
    public void deleteByRegionAndDate(Long regionId, LocalDate date) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement deleteStatement = connection.prepareStatement(WeatherSql.DELETE_BY_REGION_DATE.getMessage())) {
                deleteStatement.setLong(1, regionId);
                deleteStatement.setDate(2, Date.valueOf(date));
                deleteStatement.execute();
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather", 500);
        }
    }

    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Override
    public void updateTemperatureByRegionAndDate(Long region_id, Integer temperature, LocalDate date) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement updateStatement = connection.prepareStatement(WeatherSql.UPDATE_TEMPERATURE.getMessage())) {
                updateStatement.setInt(1, temperature);
                updateStatement.setLong(2, region_id);
                updateStatement.setDate(3, Date.valueOf(date));
                updateStatement.execute();
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather", 500);
        }
    }

    /*
    взял SERIALIZABLE, т.к. часто будут вставлять данные
     */
    @Override
    public void updateTypeByRegionAndDate(Long region_id, Long type_id, LocalDate date) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement updateStatement = connection.prepareStatement(WeatherSql.UPDATE_WEATHER.getMessage())) {
                updateStatement.setLong(1, type_id);
                updateStatement.setLong(2, region_id);
                updateStatement.setDate(3, Date.valueOf(date));
                updateStatement.execute();
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather", 500);
        }
    }

    private static Optional<WeatherDto> findIfExists(Connection connection, Long region_id, LocalDate date) throws SQLException {
        try (PreparedStatement selectStatement = connection.prepareStatement(WeatherSql.SELECT_IF_EXISTS.getMessage())) {
            selectStatement.setLong(1, region_id);
            selectStatement.setDate(2, Date.valueOf(date));
            ResultSet rs = selectStatement.executeQuery();
            Optional<WeatherNew> weatherNew = Optional.empty();
            if (rs.next()) {
                weatherNew = Optional.of(new WeatherNew(
                        rs.getLong("id"),
                        rs.getLong("region_id"),
                        rs.getLong("type_id"),
                        rs.getInt("temperature"),
                        rs.getDate("date").toLocalDate()
                ));
            }
            return WeatherMapper.optionalEntityToDto(weatherNew);
        }
    }

    public static WeatherDto insertRow(Connection connection, WeatherNew weatherNew) throws SQLException {
        Optional<WeatherDto> weatherNewDataBase = findIfExists(connection, weatherNew.getRegionId(), weatherNew.getDate());
        if (weatherNewDataBase.isEmpty()) {
            try (PreparedStatement insertStatement = connection.prepareStatement(WeatherSql.INSERT.getMessage())) {
                insertStatement.setLong(1, weatherNew.getRegionId());
                insertStatement.setLong(2, weatherNew.getTypeId());
                insertStatement.setInt(3, weatherNew.getTemperature());
                insertStatement.setDate(4, Date.valueOf(weatherNew.getDate()));
                insertStatement.execute();
                return findIfExists(connection, weatherNew.getRegionId(), weatherNew.getDate()).get();
            }
        }
    }
}
