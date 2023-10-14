package org.example.enums.jdbc;

import lombok.Getter;

@Getter
public enum RegionSql {
    SELECT("select * from region where id = ?"),
    UPDATE("update region set name = ? where id = ?"),
    DELETE("delete from region where id = ?"),
    INSERT("insert into region(id, name) values (?, ?)");
    private final String message;

    RegionSql(String message) {
        this.message = message;
    }
}
