package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "weather")
public class WeatherNew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "region_id")
    private Long region_id;

    @Column(name = "type_id")
    private Long type_id;

    @Column(name = "temperature")
    private Integer temperature;

    @Column(name = "date")
    private LocalDate date;

    public WeatherNew(Long region_id, Long type_id, Integer temperature, LocalDate date){
        this.region_id = region_id;
        this.type_id = type_id;
        this.temperature = temperature;
        this.date = date;
    }

    @Override
    public String toString() {
        return "WeatherNew{" +
                "id=" + id +
                ", region_id=" + region_id +
                ", type_id=" + type_id +
                ", temperature=" + temperature +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeatherNew that)) return false;
        return Objects.equals(region_id, that.region_id) && Objects.equals(type_id, that.type_id) && Objects.equals(temperature, that.temperature) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region_id, type_id, temperature, date);
    }
}
