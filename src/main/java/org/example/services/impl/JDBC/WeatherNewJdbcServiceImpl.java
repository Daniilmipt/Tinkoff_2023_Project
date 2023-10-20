package org.example.services.impl.JDBC;

import org.example.enums.jdbc.WeatherSql;
import org.example.exceptions.weatherApi.ResponseException;
import org.example.model.Region;
import org.example.model.WeatherNew;
import org.example.model.WeatherType;
import org.example.services.WeatherNewService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class WeatherNewJdbcServiceImpl implements WeatherNewService {
    private final DataSource dataSource;

    public WeatherNewJdbcServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public WeatherNew save(WeatherNew weatherNew) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            WeatherNew weatherNewInserted = insertRow(connection, weatherNew);
            connection.close();
            return weatherNewInserted;
        }
    }


    @Override
    public WeatherNew saveByWeatherTypeAndRegion(WeatherType weatherType, Region region, Integer temperature) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Savepoint save = connection.setSavepoint();
            try {
                Region regionInserted = RegionJdbcServiceImpl.insertRow(connection, region);
                Savepoint saveRegion = connection.setSavepoint();
                try {
                    WeatherType weatherTypeInserted = WeatherTypeJdbcServiceImpl.insertRow(connection, weatherType);
                    Savepoint saveRegionAndType = connection.setSavepoint();
                    try {
                        WeatherNew weatherNew = new WeatherNew(
                                regionInserted.getId(),
                                weatherTypeInserted.getId(),
                                temperature,
                                LocalDate.now()
                        );
                        WeatherNew weatherNewInserted = insertRow(connection, weatherNew);
                        connection.commit();
                        connection.close();
                        return weatherNewInserted;
                    } catch (Exception e) {
                        connection.rollback(saveRegionAndType);
                        throw new ResponseException(e.getMessage(), "database exception", 500);
                    }
                }
                catch (Exception e) {
                    if (!(e instanceof ResponseException)){
                        System.out.println("a");
                        connection.rollback(saveRegion);
                        connection.close();
                    }
                    throw e;
                }
            } catch (Exception e) {
                if (!(e instanceof ResponseException)){
                    System.out.println("a");
                    connection.rollback(save);
                    connection.close();
                }
                throw e;
            }
        }
    }

    @Override
    public Optional<WeatherNew> getByRegionAndDate(Long region_id, LocalDate date) throws SQLException {
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
                closeResources(selectStatement, connection);
                return weatherNew;
            }
        }
    }

    @Override
    public Optional<WeatherNew> get(Long weatherModel_id) throws SQLException {
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
                closeResources(selectStatement, connection);
                return weatherNew;
            }
        }
    }

    @Override
    public void deleteByRegion(Long regionId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try (PreparedStatement deleteStatement = connection.prepareStatement(WeatherSql.DELETE_BY_REGION.getMessage())) {
                deleteStatement.setLong(1, regionId);
                deleteStatement.execute();
                closeResources(deleteStatement, connection);
            }
        }
    }

    @Override
    public void deleteByRegionAndDate(Long regionId, LocalDate date) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement deleteStatement = connection.prepareStatement(WeatherSql.DELETE_BY_REGION_DATE.getMessage())) {
                deleteStatement.setLong(1, regionId);
                deleteStatement.setDate(2, Date.valueOf(date));
                deleteStatement.execute();
                closeResources(deleteStatement, connection);
            }
        }
    }

    @Override
    public void updateTemperatureByRegionAndDate(Long region_id, Integer temperature, LocalDate date) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement updateStatement = connection.prepareStatement(WeatherSql.UPDATE_TEMPERATURE.getMessage())) {
                updateStatement.setInt(1, temperature);
                updateStatement.setLong(2, region_id);
                updateStatement.setDate(3, Date.valueOf(date));
                updateStatement.execute();
                closeResources(updateStatement, connection);
            }
        }
    }

    @Override
    public void updateTypeByRegionAndDate(Long region_id, Long type_id, LocalDate date) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement updateStatement = connection.prepareStatement(WeatherSql.UPDATE_WEATHER.getMessage())) {
                updateStatement.setLong(1, type_id);
                updateStatement.setLong(2, region_id);
                updateStatement.setDate(3, Date.valueOf(date));
                updateStatement.execute();
                closeResources(updateStatement, connection);
            }
        }
    }

    private static void closeResources(Statement statement, Connection connection) throws SQLException {
        statement.close();
        connection.close();
    }

    private static Optional<WeatherNew> findIfExists(Connection connection, Long region_id, LocalDate date) throws SQLException {
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
            selectStatement.close();
            return weatherNew;
        }
    }

    public static WeatherNew insertRow(Connection connection, WeatherNew weatherNew) throws SQLException {
        Optional<WeatherNew> weatherNewDataBase = findIfExists(connection, weatherNew.getRegion_id(), weatherNew.getDate());
        if (weatherNewDataBase.isEmpty()) {
            try (PreparedStatement insertStatement = connection.prepareStatement(WeatherSql.INSERT.getMessage())) {
                insertStatement.setLong(1, weatherNew.getRegion_id());
                insertStatement.setLong(2, weatherNew.getType_id());
                insertStatement.setInt(3, weatherNew.getTemperature());
                insertStatement.setDate(4, Date.valueOf(weatherNew.getDate()));
                insertStatement.execute();
                insertStatement.close();
                return findIfExists(connection, weatherNew.getRegion_id(), weatherNew.getDate()).get();
            }
        }
        return weatherNewDataBase.get();
    }
}
