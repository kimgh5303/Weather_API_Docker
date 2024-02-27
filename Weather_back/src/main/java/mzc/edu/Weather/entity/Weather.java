package mzc.edu.Weather.entity;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@Entity(name="location")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Weather {
    @Id
    @Column(name="loc_id")
    private String locId;
    @Column(name="loc_name")
    private String locName;
    @Column(name="loc_latitude")
    private double locLatitude;
    @Column(name="loc_longitude")
    private double locLongitude;

    // 데이터베이스 매핑 제외
    @Transient
    private double temp;
    @Transient
    private int windDir;
    @Transient
    private double windSpeed;
    @Transient
    private int humidity;
}
