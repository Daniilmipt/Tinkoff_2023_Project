package org.example.services.impl.JDBC;

import org.example.enums.jdbc.RegionSql;
import org.example.model.Region;
import org.example.services.RegionService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Service
public class RegionJdbcServiceImpl implements RegionService {
    private final DataSource dataSource;

    public RegionJdbcServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Region save(Region region) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            Region regionInserted = insertRow(connection, region);
            connection.close();
            return regionInserted;
        }
    }

    @Override
    public Optional<Region> get(Long regionId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            try (PreparedStatement selectStatement = connection.prepareStatement(RegionSql.SELECT.getMessage())) {
                selectStatement.setLong(1, regionId);
                ResultSet rs = selectStatement.executeQuery();
                Optional<Region> region = Optional.empty();
                if (rs.next()) {
                    region = Optional.of(new Region(rs.getLong("id"), rs.getString("name")));
                }
                closeResources(selectStatement, connection);
                return region;
            }
        }
    }

    @Override
    public void delete(Long regionId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            try (PreparedStatement deleteStatement = connection.prepareStatement(RegionSql.DELETE.getMessage())) {
                deleteStatement.setLong(1, regionId);
                deleteStatement.execute();
                closeResources(deleteStatement, connection);
            }
        }
    }

    @Override
    public void update(Long regionId, String name) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            try (PreparedStatement updateStatement = connection.prepareStatement(RegionSql.UPDATE.getMessage())) {
                updateStatement.setString(1, name);
                updateStatement.setLong(2, regionId);
                updateStatement.execute();
                closeResources(updateStatement, connection);
            }
        }
    }
    
    public static Region insertRow(Connection connection, Region region) throws SQLException {
        Optional<Region> regionDataBase = findIfExists(connection, region.getName());
        if (regionDataBase.isEmpty()) {
            try(PreparedStatement insertStatement = connection.prepareStatement(RegionSql.INSERT.getMessage())) {
                insertStatement.setString(1, region.getName());
                insertStatement.execute();
                insertStatement.close();
                return findIfExists(connection, region.getName()).get();
            }
        }
        return regionDataBase.get();
    }

    private static void closeResources(Statement statement, Connection connection) throws SQLException {
        statement.close();
        connection.close();
    }

    public static Optional<Region> findIfExists(Connection connection, String name) throws SQLException {
        try (PreparedStatement selectStatement = connection.prepareStatement(RegionSql.SELECT_IF_EXISTS.getMessage())) {
            selectStatement.setString(1, name);
            ResultSet rs = selectStatement.executeQuery();
            Optional<Region> region = Optional.empty();
            if (rs.next()) {
                region = Optional.of(new Region(rs.getLong("id"), rs.getString("name")));
            }
            selectStatement.close();
            return region;
        }
    }
}
