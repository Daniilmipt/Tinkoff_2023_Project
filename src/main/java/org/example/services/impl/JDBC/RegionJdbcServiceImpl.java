package org.example.services.impl.JDBC;

import org.example.dto.RegionDto;
import org.example.enums.jdbc.RegionSql;
import org.example.mapper.RegionMapper;
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
    public RegionDto save(Region region) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            return insertRow(connection, region);
        }
    }

    @Override
    public Optional<RegionDto> get(Long regionId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            try (PreparedStatement selectStatement = connection.prepareStatement(RegionSql.SELECT.getMessage())) {
                selectStatement.setLong(1, regionId);
                ResultSet rs = selectStatement.executeQuery();
                Optional<Region> region = Optional.empty();
                if (rs.next()) {
                    region = Optional.of(new Region(rs.getLong("id"), rs.getString("name")));
                }
                return RegionMapper.optionalEntityToDto(region);
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
            }
        }
    }
    
    public static RegionDto insertRow(Connection connection, Region region) throws SQLException {
        Optional<RegionDto> regionDataBase = findIfExists(connection, region.getName());
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

    public static Optional<RegionDto> findIfExists(Connection connection, String name) throws SQLException {
        try (PreparedStatement selectStatement = connection.prepareStatement(RegionSql.SELECT_IF_EXISTS.getMessage())) {
            selectStatement.setString(1, name);
            ResultSet rs = selectStatement.executeQuery();
            Optional<Region> region = Optional.empty();
            if (rs.next()) {
                region = Optional.of(new Region(rs.getLong("id"), rs.getString("name")));
            }
            selectStatement.close();
            return RegionMapper.optionalEntityToDto(region);
        }
    }
}
