package org.example.enums.jdbc;

import lombok.Getter;

@Getter
public enum WeatherTypeSql {
    SELECT("select * from weather_type where id = ?"),
    UPDATE("update weather_type set description = ? where id = ?"),
    DELETE("delete from weather_type where id = ?"),
    INSERT("insert into weather_type(id, description) values (?, ?)");
    private final String message;

    WeatherTypeSql(String message) {
        this.message = message;
    }
}
