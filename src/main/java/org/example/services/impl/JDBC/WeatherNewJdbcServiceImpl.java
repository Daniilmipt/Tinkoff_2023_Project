package org.example.services.impl.JDBC;

import org.example.enums.jdbc.WeatherSql;
import org.example.model.WeatherNew;
import org.example.services.WeatherNewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public WeatherNew save(WeatherNew weatherNew) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement insertStatement = connection.prepareStatement(WeatherSql.INSERT.getMessage());
        insertStatement.setLong(1, weatherNew.getId());
        insertStatement.setLong(2, weatherNew.getRegion_id());
        insertStatement.setLong(3, weatherNew.getType_id());
        insertStatement.setInt(4, weatherNew.getTemperature());
        insertStatement.setDate(5, Date.valueOf(weatherNew.getDate()));
        insertStatement.execute();
        closeResources(insertStatement, connection);
        return weatherNew;
    }

    @Override
    public Optional<WeatherNew> getByRegionAndDate(Long region_id, LocalDate date) throws SQLException {
        Connection connection = dataSource.getConnection();
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

    @Transactional
    @Override
    public void deleteByRegion(Long regionId) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement deleteStatement = connection.prepareStatement(WeatherSql.DELETE_BY_REGION.getMessage());
        deleteStatement.setLong(1, regionId);
        deleteStatement.execute();
        closeResources(deleteStatement, connection);
    }

    @Transactional
    @Override
    public void deleteByRegionAndDate(Long regionId, LocalDate date) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement deleteStatement = connection.prepareStatement(WeatherSql.DELETE_BY_REGION_DATE.getMessage());
        deleteStatement.setLong(1, regionId);
        deleteStatement.setDate(2, Date.valueOf(date));
        deleteStatement.execute();
        closeResources(deleteStatement, connection);
    }

    @Transactional
    @Override
    public void updateTemperatureByRegionAndDate(Long region_id, Integer temperature, LocalDate date) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement updateStatement = connection.prepareStatement(WeatherSql.UPDATE_TEMPERATURE.getMessage());
        updateStatement.setInt(1, temperature);
        updateStatement.setLong(2, region_id);
        updateStatement.setDate(3, Date.valueOf(date));
        updateStatement.execute();
        closeResources(updateStatement, connection);
    }

    @Transactional
    @Override
    public void updateTypeByRegionAndDate(Long region_id, Long type_id, LocalDate date) throws SQLException {
        Connection connection = dataSource.getConnection();
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
}
