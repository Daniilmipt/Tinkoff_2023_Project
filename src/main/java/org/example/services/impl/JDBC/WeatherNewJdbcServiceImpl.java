package org.example.services.impl.JDBC;

import org.example.enums.jdbc.WeatherSql;
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
    private final WeatherTypeJdbcServiceImpl weatherTypeJdbcService;
    private final RegionJdbcServiceImpl regionJdbcService;

    public WeatherNewJdbcServiceImpl(DataSource dataSource, WeatherTypeJdbcServiceImpl weatherTypeJdbcService, RegionJdbcServiceImpl regionJdbcService) {
        this.dataSource = dataSource;
        this.weatherTypeJdbcService = weatherTypeJdbcService;
        this.regionJdbcService = regionJdbcService;
    }

    @Override
    public WeatherNew save(WeatherNew weatherNew) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        Optional<WeatherNew> weatherNewDataBase = findIfExists(connection, weatherNew.getRegion_id(), weatherNew.getDate());
        if (weatherNewDataBase.isEmpty()) {
            PreparedStatement insertStatement = connection.prepareStatement(WeatherSql.INSERT.getMessage());
            insertStatement.setLong(1, weatherNew.getRegion_id());
            insertStatement.setLong(2, weatherNew.getType_id());
            insertStatement.setInt(3, weatherNew.getTemperature());
            insertStatement.setDate(4, Date.valueOf(weatherNew.getDate()));
            insertStatement.execute();
            WeatherNew weatherNewInserted = findIfExists(connection, weatherNew.getRegion_id(), weatherNew.getDate()).get();
            closeResources(insertStatement, connection);
            return weatherNewInserted;
        }
        connection.close();
        return weatherNewDataBase.get();
    }


    @Override
    public void saveByWeatherTypeAndRegion(WeatherType weatherType, Region region, Integer temperature) throws SQLException {
        Region regionDataBase = regionJdbcService.save(region);
        WeatherType weatherTypeDataBase = weatherTypeJdbcService.save(weatherType);
        save(new WeatherNew(regionDataBase.getId(), weatherTypeDataBase.getId(), temperature, LocalDate.now()));
    }

    @Override
    public Optional<WeatherNew> getByRegionAndDate(Long region_id, LocalDate date) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

        PreparedStatement selectStatement = connection.prepareStatement(WeatherSql.SELECT_BY_REGION_DATE.getMessage());
        selectStatement.setLong(1, region_id);
        selectStatement.setDate(2, Date.valueOf(date));
        ResultSet rs = selectStatement.executeQuery();
        Optional<WeatherNew> weatherNew = Optional.empty();
        if (rs.next()){
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

    @Override
    public Optional<WeatherNew> get(Long weatherModel_id) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

        PreparedStatement selectStatement = connection.prepareStatement(WeatherSql.SELECT.getMessage());
        selectStatement.setLong(1, weatherModel_id);
        ResultSet rs = selectStatement.executeQuery();
        Optional<WeatherNew> weatherNew = Optional.empty();
        if (rs.next()){
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

    @Override
    public void deleteByRegion(Long regionId) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        PreparedStatement deleteStatement = connection.prepareStatement(WeatherSql.DELETE_BY_REGION.getMessage());
        deleteStatement.setLong(1, regionId);
        deleteStatement.execute();
        closeResources(deleteStatement, connection);
    }

    @Override
    public void deleteByRegionAndDate(Long regionId, LocalDate date) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        PreparedStatement deleteStatement = connection.prepareStatement(WeatherSql.DELETE_BY_REGION_DATE.getMessage());
        deleteStatement.setLong(1, regionId);
        deleteStatement.setDate(2, Date.valueOf(date));
        deleteStatement.execute();
        closeResources(deleteStatement, connection);
    }

    @Override
    public void updateTemperatureByRegionAndDate(Long region_id, Integer temperature, LocalDate date) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        PreparedStatement updateStatement = connection.prepareStatement(WeatherSql.UPDATE_TEMPERATURE.getMessage());
        updateStatement.setInt(1, temperature);
        updateStatement.setLong(2, region_id);
        updateStatement.setDate(3, Date.valueOf(date));
        updateStatement.execute();
        closeResources(updateStatement, connection);
    }

    @Override
    public void updateTypeByRegionAndDate(Long region_id, Long type_id, LocalDate date) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        PreparedStatement updateStatement = connection.prepareStatement(WeatherSql.UPDATE_WEATHER.getMessage());
        updateStatement.setLong(1, type_id);
        updateStatement.setLong(2, region_id);
        updateStatement.setDate(3, Date.valueOf(date));
        updateStatement.execute();
        closeResources(updateStatement, connection);
    }
    private static void closeResources(Statement statement, Connection connection) throws SQLException {
        statement.close();
        connection.close();
    }

    private Optional<WeatherNew> findIfExists(Connection connection, Long region_id, LocalDate date) throws SQLException {
        PreparedStatement selectStatement = connection.prepareStatement(WeatherSql.SELECT_IF_EXISTS.getMessage());
        selectStatement.setLong(1, region_id);
        selectStatement.setDate(2, Date.valueOf(date));
        ResultSet rs = selectStatement.executeQuery();
        Optional<WeatherNew> weatherNew = Optional.empty();
        if (rs.next()){
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
