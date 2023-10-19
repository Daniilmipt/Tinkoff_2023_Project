package org.example.enums.jdbc;

import lombok.Getter;

@Getter
public enum RegionSql {
    SELECT("select * from region where id = ?"),
    SELECT_IF_EXISTS("select * from region where name = ?"),
    UPDATE("update region set name = ? where id = ?"),
    DELETE("delete from region where id = ?"),
    INSERT("insert into region(name) values (?)");
    private final String message;

    RegionSql(String message) {
        this.message = message;
    }
}
