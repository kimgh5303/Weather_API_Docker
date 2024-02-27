package mzc.edu.Weather;

import mzc.edu.Weather.dto.CoordDto;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class Geocoder {

    @Value("${coord.key}")
    String apikey;
    String epsg = "epsg:4326";

    // 메서드나 생성자 추가
    public void someMethod(CoordDto coordDto) {
        if (coordDto.getAddress() == null) {
            throw new IllegalArgumentException("주소가 null입니다.");
        }

        StringBuilder sb = new StringBuilder("https://api.vworld.kr/req/address");
        sb.append("?service=address");
        sb.append("&request=getCoord");
        sb.append("&format=json");
        sb.append("&crs=" + epsg);
        sb.append("&key=" + apikey);
        sb.append("&type=" + "ROAD");
        sb.append("&address=" + URLEncoder.encode(coordDto.getAddress(), StandardCharsets.UTF_8));

        try {
            URL url = new URL(sb.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

            JSONParser jspa = new JSONParser();
            JSONObject jsob = (JSONObject) jspa.parse(reader);
            JSONObject jsrs = (JSONObject) jsob.get("response");
            JSONObject jsResult = (JSONObject) jsrs.get("result");
            JSONObject jspoitn = (JSONObject) jsResult.get("point");

            double longitude = Double.valueOf((String) jspoitn.get("x")).doubleValue();
            double latitude = Double.valueOf((String) jspoitn.get("y")).doubleValue();

            coordDto.setLongitude(longitude);
            coordDto.setLatitude(latitude);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
