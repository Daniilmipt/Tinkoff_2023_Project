package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

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
    private Long regionId;

    @Column(name = "type_id")
    private Long typeId;

    @Column(name = "temperature")
    private Integer temperature;

    @Column(name = "date")
    private LocalDate date;

    public WeatherNew(Long regionId, Long typeId, Integer temperature, LocalDate date){
        this.regionId = regionId;
        this.typeId = typeId;
        this.temperature = temperature;
        this.date = date;
    }
}
