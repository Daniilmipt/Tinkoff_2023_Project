package org.example.enums.jdbc;

import lombok.Getter;

@Getter
public enum RegionSql {
    SELECT("select * from region where id = ? limit 1"),
    SELECT_IF_EXISTS("select * from region where name = ? limit 1"),
    UPDATE("update region set name = ? where id = ?"),
    DELETE("delete from region where id = ?"),
    INSERT("insert into region(name) values (?)");
    private final String message;

    RegionSql(String message) {
        this.message = message;
    }
}
