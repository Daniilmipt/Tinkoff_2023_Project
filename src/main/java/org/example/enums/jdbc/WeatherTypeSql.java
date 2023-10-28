package org.example.enums.jdbc;

import lombok.Getter;

@Getter
public enum WeatherTypeSql {
    SELECT("select * from weather_type where id = ?"),
    SELECT_IF_EXISTS("select * from weather_type where description = ? limit 1"),
    ROWS_COUNT("select count(*) as cnt from weather_type where description = ?"),
    UPDATE("update weather_type set description = ? where id = ?"),
    DELETE("delete from weather_type where id = ?"),
    INSERT("insert into weather_type(description) values (?)");
    private final String message;

    WeatherTypeSql(String message) {
        this.message = message;
    }
}
