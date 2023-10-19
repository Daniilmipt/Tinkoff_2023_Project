package org.example.enums.jdbc;

import lombok.Getter;

@Getter
public enum WeatherSql {
    SELECT_BY_REGION_DATE("select * from weather where region_id = ? and date = ?"),
    SELECT("select * from weather where id = ?"),
    SELECT_IF_EXISTS("select * from weather where region_id = ? and date = ?"),
    UPDATE_TEMPERATURE("update weather set temperature = ? where region_id = ? and date = ?"),
    UPDATE_WEATHER("update weather set type_id = ? where region_id = ? and date = ?"),
    DELETE_BY_REGION("delete from weather where region_id = ?"),
    DELETE_BY_REGION_DATE("delete from weather where region_id = ? and date = ?"),
    INSERT("insert into weather(region_id, type_id, temperature, date) values (?, ?, ?, ?)");
    private final String message;

    WeatherSql(String message) {
        this.message = message;
    }
}
