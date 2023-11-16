package org.example.services.impl.JDBC;

import org.example.dto.WeatherTypeDto;
import org.example.enums.jdbc.WeatherTypeSql;
import org.example.mapper.WeatherTypeMapper;
import org.example.model.WeatherType;
import org.example.services.WeatherTypeService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class WeatherTypeJdbcServiceImpl implements WeatherTypeService {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public WeatherTypeJdbcServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /*
    REPEATABLE_READ, т.к. не учитываю фантомное чтение,
     потому что в таблицы редко что-то добавляют
     */
    @Override
    public WeatherTypeDto save(WeatherType weatherType) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            return insertRow(connection, weatherType);
        }
    }

    /*
    READ_UNCOMMITTED, т.к. в таблицы нечасто вносят изменения посредством update
     */
    @Override
    public Optional<WeatherTypeDto> get(Long weatherTypeId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

            try (PreparedStatement selectStatement = connection.prepareStatement(WeatherTypeSql.SELECT.getMessage())) {
                selectStatement.setLong(1, weatherTypeId);
                ResultSet rs = selectStatement.executeQuery();
                Optional<WeatherType> weatherType = Optional.empty();
                if (rs.next()) {
                    weatherType = Optional.of(new WeatherType(rs.getLong("id"), rs.getString("description")));
                }
                return WeatherTypeMapper.optionalEntityToDto(weatherType);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather_type", 500);
        }
    }

    /*
    REPEATABLE_READ, т.к. не учитываю фантомное чтение,
     потому что в таблицы редко что-то добавляют
     */
    @Override
    public void delete(Long weatherTypeId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            try (PreparedStatement deleteStatement = connection.prepareStatement(WeatherTypeSql.DELETE.getMessage())) {
                deleteStatement.setLong(1, weatherTypeId);
                deleteStatement.execute();
            }
            catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause();
                throw new SqlException(message, "table weather_type", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather_type", 500);
        }
    }

    /*
    REPEATABLE_READ, т.к. не учитываю фантомное чтение,
     потому что в таблицы редко что-то добавляют
     */
    @Override
    public void update(Long weatherTypeId, String description) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            try (PreparedStatement updateStatement = connection.prepareStatement(WeatherTypeSql.UPDATE.getMessage())) {
                updateStatement.setString(1, description);
                updateStatement.setLong(2, weatherTypeId);
                updateStatement.execute();
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather_type", 500);
        }
    }

    public static WeatherTypeDto insertRow(Connection connection, WeatherType weatherType) throws SQLException {
        Optional<WeatherTypeDto> weatherTypeDataBase = findIfExists(connection, weatherType.getDescription());
        if (weatherTypeDataBase.isEmpty()) {
            try (PreparedStatement insertStatement = connection.prepareStatement(WeatherTypeSql.INSERT.getMessage())) {
                insertStatement.setString(1, weatherType.getDescription());
                insertStatement.execute();
                return findIfExists(connection, weatherType.getDescription()).get();
            }
        }
        return weatherTypeDataBase.get();
    }

    public static Optional<WeatherTypeDto> findIfExists(Connection connection, String description) throws SQLException {
        try (PreparedStatement selectStatement = connection.prepareStatement(WeatherTypeSql.SELECT_IF_EXISTS.getMessage())) {
            selectStatement.setString(1, description);
            ResultSet rs = selectStatement.executeQuery();
            Optional<WeatherType> weatherType = Optional.empty();
            if (rs.next()) {
                weatherType = Optional.of(new WeatherType(rs.getLong("id"), rs.getString("description")));
            }
            return WeatherTypeMapper.optionalEntityToDto(weatherType);
        }
    }
}
