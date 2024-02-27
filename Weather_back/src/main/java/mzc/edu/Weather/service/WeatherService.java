package mzc.edu.Weather.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mzc.edu.Weather.ForeCast;
import mzc.edu.Weather.dto.AllResDto;
import mzc.edu.Weather.dto.CoordDto;
import mzc.edu.Weather.entity.Weather;
import mzc.edu.Weather.error.ErrorCode;
import mzc.edu.Weather.rep.WeatherRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherRepository weatherRepository;
    private final ForeCast foreCast;

    // 위치 조회
    public ResponseEntity<AllResDto> getLocation() throws IOException, ParserConfigurationException, SAXException {
        List<Weather> weather = weatherRepository.findAll();
        return new ResponseEntity<>(new AllResDto(true, "조회 완료", foreCast.fetchForeCast(weather)), HttpStatus.OK);
    }

    // 위치 추가
    @Transactional
    public ResponseEntity<AllResDto> addLocation(CoordDto coordDto) {
        Weather weather = coordDto.toEntity();
        weatherRepository.addLocation(coordDto.getLocid(), weather.getLocName(), weather.getLocLatitude(), weather.getLocLongitude());
        return new ResponseEntity<>(new AllResDto(true, "추가 완료", weather), HttpStatus.OK);
    }

    // 위치 업데이트
    @Transactional
    public ResponseEntity<AllResDto> updateLocation(CoordDto coordDto) {
        Weather weather = coordDto.toEntity();
        weatherRepository.updateLocation(weather.getLocId(), weather.getLocName(), weather.getLocLatitude(), weather.getLocLongitude());
        return new ResponseEntity<>(new AllResDto(true, "수정 완료", weather), HttpStatus.OK);
    }

    // 위치 삭제
    public ResponseEntity<AllResDto> deleteLocation(String id) {
        weatherRepository.deleteById(id);
        return new ResponseEntity<>(new AllResDto(true, "삭제 완료"), HttpStatus.OK);
    }
}
