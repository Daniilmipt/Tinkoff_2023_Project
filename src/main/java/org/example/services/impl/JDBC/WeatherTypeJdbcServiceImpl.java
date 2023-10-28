package org.example.services.impl.JDBC;

import org.example.enums.jdbc.RegionSql;
import org.example.enums.jdbc.WeatherSql;
import org.example.enums.jdbc.WeatherTypeSql;
import org.example.exceptions.SqlException;
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

    @Override
    public WeatherType save(WeatherType weatherType) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            return insertRow(jdbcTemplate, weatherType);
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather_type", 500);
        }
    }

    @Override
    public Optional<WeatherType> get(Long weatherTypeId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            try {
                return Optional.ofNullable(jdbcTemplate.queryForObject(
                        WeatherTypeSql.SELECT.getMessage(), new Object[]{weatherTypeId}, (rs, rowNum) ->
                                new WeatherType(
                                        rs.getLong("id"),
                                        rs.getString("description")
                                )
                ));
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause();
                throw new SqlException(message, "table weather_type", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather_type", 500);
        }
    }

    @Override
    public void delete(Long weatherTypeId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            try {
                jdbcTemplate.update(WeatherTypeSql.DELETE.getMessage(), weatherTypeId);
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

    @Override
    public Integer update(Long weatherTypeId, String description) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            try {
                jdbcTemplate.update(WeatherTypeSql.UPDATE.getMessage(), description, weatherTypeId);
                return jdbcTemplate.queryForObject(WeatherSql.ROWS_COUNT.getMessage(),
                        new Object[]{description}, (rs, rowNum) ->
                                rs.getInt("cnt")
                );
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause();
                throw new SqlException(message, "table weather_type", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table weather_type", 500);
        }
    }

    public static WeatherType insertRow(JdbcTemplate jdbcTemplate, WeatherType weatherType) throws SQLException {
        try {
            jdbcTemplate.update(RegionSql.INSERT.getMessage(), weatherType.getDescription());
            return findIfExists(jdbcTemplate, weatherType.getDescription()).get();
        } catch (DataAccessException e){
            return findIfExists(jdbcTemplate, weatherType.getDescription()).get();
        }
    }

    public static Optional<WeatherType> findIfExists(JdbcTemplate jdbcTemplate, String description) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    WeatherTypeSql.SELECT_IF_EXISTS.getMessage(), new Object[]{description}, (rs, rowNum) ->
                            new WeatherType(
                                    rs.getLong("id"),
                                    rs.getString("description")
                            )
            ));
        } catch (DataAccessException e){
            return Optional.empty();
        }
    }
}
