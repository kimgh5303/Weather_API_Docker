package mzc.edu.Weather;

import mzc.edu.Weather.entity.Weather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ForeCast {
    @Value("${forecast.key}")
    String apikey;
    static int x;               // 격자점 X
    static int y;               // 격자점 Y

    // 위,경도 좌표 -> 격자점 X, Y (기상청 제공)
    public void changeGrid(Weather weather) {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)
    
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        double DEGRAD = Math.PI / 180.0;
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;
    
        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        double ra = Math.tan(Math.PI * 0.25 + (weather.getLocLatitude()) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = weather.getLocLongitude() * DEGRAD - olon;
                if (theta > Math.PI) theta -= 2.0 * Math.PI;
                if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;
        x = (int)Math.floor(ra * Math.sin(theta) + XO + 0.5);
        y = (int)Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
    }

    // 기상청 api로 데이터 가져오기
    public List<Weather> fetchForeCast(List<Weather> weatherList) throws IOException, ParserConfigurationException, SAXException {

        LocalDateTime currentDateTime = LocalDateTime.now();
        // 현재 시간에서 가장 가까운 이전 정시 계산
        int currentHour = currentDateTime.getHour();
        int closestHour = (currentHour - 1 + 24) % 24; // 이전 정시 계산
        // 형식화하여 출력 (뒤에 "00" 붙이기)
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH00");
        // 정시로부터 1시간 전의 시간을 계산 -> 30분에 업데이트되므로
        LocalDateTime previousHour = currentDateTime.withHour(closestHour);
        String formattedDate = previousHour.format(dateFormatter);
        String formattedHour = previousHour.format(hourFormatter);

        // 각 주소별로 객체에 담기 위해 반복문 실행
        for (int i = 0; i < weatherList.size(); i++) {
            Weather weather = weatherList.get(i);
            changeGrid(weather);

            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + apikey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("XML", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(formattedDate, "UTF-8")); /*‘00년 0월 00일 발표*/
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(formattedHour, "UTF-8")); /*00시 발표(정시단위) */
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(x), "UTF-8")); /*예보지점의 X 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(y), "UTF-8")); /*예보지점의 Y 좌표값*/

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            String xmlData = sb.toString();
            
            // XML에서 원하는 카테고리 추출
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                ByteArrayInputStream input = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
                Document document = builder.parse(input);

                // 기온 카테고리의 obsrValue 추출
                NodeList itemList = document.getElementsByTagName("item");
                for (int index = 0; index < itemList.getLength(); index++) {
                    Node itemNode = itemList.item(index);

                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element itemElement = (Element) itemNode;

                        // 카테고리 추출
                        NodeList categoryNodeList = itemElement.getElementsByTagName("category");
                        Node categoryNode = categoryNodeList.item(0);
                        String category = categoryNode.getTextContent();

                        // obsrValue 추출
                        NodeList obsrValueNodeList = itemElement.getElementsByTagName("obsrValue");
                        Node obsrValueNode = obsrValueNodeList.item(0);
                        String obsrValue = obsrValueNode.getTextContent();

                        // 각 카테고리에 따라 필드에 할당
                        switch (category) {
                            case "T1H":
                                weather.setTemp(Double.parseDouble(obsrValue));
                                System.out.println("T1H obsrValue: " + obsrValue);
                                break;
                            case "VEC":
                                weather.setWindDir(Integer.parseInt(obsrValue));
                                System.out.println("VEC obsrValue: " + obsrValue);
                                break;
                            case "WSD":
                                weather.setWindSpeed(Double.parseDouble(obsrValue));
                                System.out.println("WSD fcstValue: " + obsrValue);
                                break;
                            case "REH":
                                weather.setHumidity(Integer.parseInt(obsrValue));
                                System.out.println("REH fcstValue: " + obsrValue);
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return weatherList;
    }


    //----------------------------------------------------------------------------------------------------------------------------------
    // 기상청 api 테스트
    public String testForeCast() throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" +apikey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("XML", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode("20240110", "UTF-8")); /*‘21년 6월 28일 발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0400", "UTF-8")); /*06시 발표(정시단위) */
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("60", "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); /*예보지점의 Y 좌표값*/

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        
        return sb.toString();
    }
}
