package mzc.edu.Weather.dto;

import lombok.Getter;
import lombok.Setter;
import mzc.edu.Weather.entity.Weather;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class CoordDto {
    private String locid;
    private String address;
    private double latitude;
    private double longitude;

    public Weather toEntity(){
        return Weather.builder()
                .locId(locid)
                .locName(address)
                .locLatitude(latitude)
                .locLongitude(longitude)
                .build();
    }
}
