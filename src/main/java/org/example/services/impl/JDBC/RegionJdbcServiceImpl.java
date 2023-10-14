package org.example.services.impl.JDBC;

import org.example.enums.jdbc.RegionSql;
import org.example.model.Region;
import org.example.services.RegionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Service
public class RegionJdbcServiceImpl implements RegionService {
    private final DataSource dataSource;

    public RegionJdbcServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional
    @Override
    public Region save(Region region) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement insertStatement = connection.prepareStatement(RegionSql.INSERT.getMessage());
        insertStatement.setLong(1, region.getId());
        insertStatement.setString(2, region.getName());
        insertStatement.execute();
        closeResources(insertStatement, connection);
        return region;
    }

    @Override
    public Optional<Region> get(Long regionId) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement selectStatement = connection.prepareStatement(RegionSql.SELECT.getMessage());
        selectStatement.setLong(1, regionId);
        ResultSet rs = selectStatement.executeQuery();
        Optional<Region> region = Optional.empty();
        if (rs.next()){
            region = Optional.of(new Region(rs.getLong("id"), rs.getString("name")));
        }
        closeResources(selectStatement, connection);
        return region;
    }

    @Transactional
    @Override
    public void delete(Long regionId) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement deleteStatement = connection.prepareStatement(RegionSql.DELETE.getMessage());
        deleteStatement.setLong(1, regionId);
        deleteStatement.execute();
        closeResources(deleteStatement, connection);
    }

    @Transactional
    @Override
    public void update(Long regionId, String name) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement updateStatement = connection.prepareStatement(RegionSql.UPDATE.getMessage());
        updateStatement.setString(1, name);
        updateStatement.setLong(2, regionId);
        updateStatement.execute();
        closeResources(updateStatement, connection);
    }

    private static void closeResources(Statement statement, Connection connection) throws SQLException {
        statement.close();
        connection.close();
    }
}
