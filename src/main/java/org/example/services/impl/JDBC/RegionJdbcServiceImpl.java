package org.example.services.impl.JDBC;

import org.example.enums.jdbc.RegionSql;
import org.example.exceptions.SqlException;
import org.example.model.Region;
import org.example.services.RegionService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class RegionJdbcServiceImpl implements RegionService {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public RegionJdbcServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Region save(Region region) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            return insertRow(jdbcTemplate, region);
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table region", 500);
        }
    }

    @Override
    public Optional<Region> get(Long regionId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            try {
                return Optional.ofNullable(jdbcTemplate.queryForObject(
                        RegionSql.SELECT.getMessage(), new Object[]{regionId}, (rs, rowNum) ->
                                new Region(
                                        rs.getLong("id"),
                                        rs.getString("name")
                                )
                ));
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause();
                throw new SqlException(message, "table region", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table region", 500);
        }
    }

    @Override
    public void delete(Long regionId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            try {
                jdbcTemplate.update(RegionSql.DELETE.getMessage(), regionId);
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause();
                throw new SqlException(message, "table region", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table region", 500);
        }
    }

    @Override
    public void update(Long regionId, String name) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            try {
                jdbcTemplate.update(RegionSql.UPDATE.getMessage(), name, regionId);
            } catch (DataAccessException e){
                String message = "Class: " + e.getClass() + "; " + e.getCause();
                throw new SqlException(message, "table region", 500);
            }
        } catch (SQLException e){
            String message = "Class: " + e.getClass() + "; " + e.getCause();
            throw new SqlException(message, "table region", 500);
        }
    }
    
    public static Region insertRow(JdbcTemplate jdbcTemplate, Region region) {
        try {
            jdbcTemplate.update(RegionSql.INSERT.getMessage(), region.getName());
            return findIfExists(jdbcTemplate, region.getName()).get();
        } catch (DataAccessException e){
            return findIfExists(jdbcTemplate, region.getName()).get();
        }
    }

    public static Optional<Region> findIfExists(JdbcTemplate jdbcTemplate, String name) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    RegionSql.SELECT_IF_EXISTS.getMessage(), new Object[]{name}, (rs, rowNum) ->
                            new Region(
                                    rs.getLong("id"),
                                    rs.getString("name")
                            )
            ));
        } catch (DataAccessException e){
            return Optional.empty();
        }
    }
}
