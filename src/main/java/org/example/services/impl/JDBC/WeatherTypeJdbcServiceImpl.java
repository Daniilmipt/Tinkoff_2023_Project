package org.example.services.impl.JDBC;

import org.example.enums.jdbc.WeatherTypeSql;
import org.example.model.WeatherType;
import org.example.services.WeatherTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Service
public class WeatherTypeJdbcServiceImpl implements WeatherTypeService {
    private final DataSource dataSource;

    public WeatherTypeJdbcServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional
    @Override
    public WeatherType save(WeatherType weatherType) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement insertStatement = connection.prepareStatement(WeatherTypeSql.INSERT.getMessage());
        insertStatement.setLong(1, weatherType.getId());
        insertStatement.setString(2, weatherType.getDescription());
        insertStatement.execute();
        closeResources(insertStatement, connection);
        return weatherType;
    }

    @Override
    public Optional<WeatherType> get(Long weatherTypeId) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement selectStatement = connection.prepareStatement(WeatherTypeSql.SELECT.getMessage());
        selectStatement.setLong(1, weatherTypeId);
        ResultSet rs = selectStatement.executeQuery();
        Optional<WeatherType> weatherType = Optional.empty();
        if (rs.next()){
            weatherType = Optional.of(new WeatherType(rs.getLong("id"), rs.getString("description")));
        }
        closeResources(selectStatement, connection);
        return weatherType;
    }

    @Transactional
    @Override
    public void delete(Long weatherTypeId) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement deleteStatement = connection.prepareStatement(WeatherTypeSql.DELETE.getMessage());
        deleteStatement.setLong(1, weatherTypeId);
        deleteStatement.execute();
        closeResources(deleteStatement, connection);
    }

    @Transactional
    @Override
    public void update(Long weatherTypeId, String description) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement updateStatement = connection.prepareStatement(WeatherTypeSql.UPDATE.getMessage());
        updateStatement.setString(1, description);
        updateStatement.setLong(2, weatherTypeId);
        updateStatement.execute();
        closeResources(updateStatement, connection);
    }

    private static void closeResources(Statement statement, Connection connection) throws SQLException {
        statement.close();
        connection.close();
    }
}
