package mzc.edu.Weather.controller;

import lombok.RequiredArgsConstructor;
import mzc.edu.Weather.FileName;
import mzc.edu.Weather.ForeCast;
import mzc.edu.Weather.Geocoder;
import mzc.edu.Weather.dto.AllResDto;
import mzc.edu.Weather.dto.CoordDto;
import mzc.edu.Weather.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@RestController
@RequestMapping("/weathers/locations")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;
    private final Geocoder geocoder;
    private final CoordDto coordDto;
    private final FileName fileName;
    private final ForeCast foreCast;

    // 위치 조회
    @GetMapping
    public ResponseEntity<AllResDto> getLocation() throws IOException, ParserConfigurationException, SAXException {
        return weatherService.getLocation();
    }

    // 위치 추가
    @PostMapping
    public ResponseEntity<AllResDto> addLocation(String address){
        coordDto.setLocid(fileName.createfileName());
        coordDto.setAddress(address);
        geocoder.someMethod(coordDto);
        return weatherService.addLocation(coordDto);
    }

    // 위치 업데이트
    @PutMapping()
    public ResponseEntity<AllResDto> updateLocation(String id, String address){
        coordDto.setLocid(id);
        coordDto.setAddress(address);
        geocoder.someMethod(coordDto);
        return weatherService.updateLocation(coordDto);
    }

    // 위치 삭제
    @DeleteMapping("/{locationId}")
    public ResponseEntity<AllResDto> deleteLocation(@PathVariable("locationId") String id) {
        return weatherService.deleteLocation(id);
    }

    // 기상청 api 테스트
    @GetMapping("/forecasts")
    public String fetchForeCast() throws IOException {
        return foreCast.testForeCast();
    }
}
