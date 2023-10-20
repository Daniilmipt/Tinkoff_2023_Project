package org.example.services.impl.JDBC;

import org.example.enums.jdbc.WeatherTypeSql;
import org.example.model.WeatherType;
import org.example.services.WeatherTypeService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Service
public class WeatherTypeJdbcServiceImpl implements WeatherTypeService {
    private final DataSource dataSource;

    public WeatherTypeJdbcServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public WeatherType save(WeatherType weatherType) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            WeatherType weatherTypeInserted = insertRow(connection, weatherType);
            connection.close();
            return weatherTypeInserted;
        }
    }

    @Override
    public Optional<WeatherType> get(Long weatherTypeId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

            try (PreparedStatement selectStatement = connection.prepareStatement(WeatherTypeSql.SELECT.getMessage())) {
                selectStatement.setLong(1, weatherTypeId);
                ResultSet rs = selectStatement.executeQuery();
                Optional<WeatherType> weatherType = Optional.empty();
                if (rs.next()) {
                    weatherType = Optional.of(new WeatherType(rs.getLong("id"), rs.getString("description")));
                }
                closeResources(selectStatement, connection);
                return weatherType;
            }
        }
    }

    @Override
    public void delete(Long weatherTypeId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            try (PreparedStatement deleteStatement = connection.prepareStatement(WeatherTypeSql.DELETE.getMessage())) {
                deleteStatement.setLong(1, weatherTypeId);
                deleteStatement.execute();
                closeResources(deleteStatement, connection);
            }
        }
    }

    @Override
    public void update(Long weatherTypeId, String description) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            try (PreparedStatement updateStatement = connection.prepareStatement(WeatherTypeSql.UPDATE.getMessage())) {
                updateStatement.setString(1, description);
                updateStatement.setLong(2, weatherTypeId);
                updateStatement.execute();
                closeResources(updateStatement, connection);
            }
        }
    }

    public static WeatherType insertRow(Connection connection, WeatherType weatherType) throws SQLException {
        Optional<WeatherType> weatherTypeDataBase = findIfExists(connection, weatherType.getDescription());
        if (weatherTypeDataBase.isEmpty()) {
            try (PreparedStatement insertStatement = connection.prepareStatement(WeatherTypeSql.INSERT.getMessage())) {
                insertStatement.setString(1, weatherType.getDescription());
                insertStatement.execute();
                insertStatement.close();
                return findIfExists(connection, weatherType.getDescription()).get();
            }
        }
        return weatherTypeDataBase.get();
    }

    private static void closeResources(Statement statement, Connection connection) throws SQLException {
        statement.close();
        connection.close();
    }

    public static Optional<WeatherType> findIfExists(Connection connection, String description) throws SQLException {
        try (PreparedStatement selectStatement = connection.prepareStatement(WeatherTypeSql.SELECT_IF_EXISTS.getMessage())) {
            selectStatement.setString(1, description);
            ResultSet rs = selectStatement.executeQuery();
            Optional<WeatherType> weatherType = Optional.empty();
            if (rs.next()) {
                weatherType = Optional.of(new WeatherType(rs.getLong("id"), rs.getString("description")));
            }
            return weatherType;
        }
    }
}
