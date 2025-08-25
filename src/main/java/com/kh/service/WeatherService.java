package com.kh.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${weather.api.key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> getSeoulWeather() {
        // 날씨 API 호출을 위한 기본 URL
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

        // API 갱신 주기에 맞춰 현재 날짜와 시간 설정
        LocalDateTime now = LocalDateTime.now();
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = "0800"; // 예시로 오전 8시를 사용

        try {
            // 서비스 키를 URL 인코딩하여 안전하게 사용
            String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);

            // API URL에 파라미터 추가
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("serviceKey", encodedServiceKey) // 인코딩된 키 사용
                    .queryParam("numOfRows", "50")
                    .queryParam("pageNo", "1")
                    .queryParam("dataType", "JSON")
                    .queryParam("base_date", baseDate)
                    .queryParam("base_time", baseTime)
                    .queryParam("nx", "98") // 대구광역시 동구
                    .queryParam("ny", "76") // 대구광역시 동구
                    .build()
                    .toUriString();

            // 외부 API 호출
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String result = response.getBody();

            // JSON 파싱 및 원하는 데이터 추출
            JSONObject json = new JSONObject(result);
            JSONArray arr = json.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

            // 콘솔에 날씨 정보 출력 (기존 자바 코드의 로직)
            System.out.println("--- 날씨 정보 ---");
            arr.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                String category = obj.getString("category");
                String value = obj.getString("fcstValue");

                switch (category) {
                    case "TMP":
                        System.out.println("온도: " + value + "℃");
                        break;
                    case "TMX":
                        System.out.println("최고온도: " + value + "℃");
                        break;
                    case "TMN":
                        System.out.println("최저온도: " + value + "℃");
                        break;
                    case "SKY":
                        String skyStatus = "";
                        switch (value) {
                            case "1": skyStatus = "맑음"; break;
                            case "3": skyStatus = "구름많음"; break;
                            case "4": skyStatus = "흐림"; break;
                        }
                        System.out.println("하늘 상태: " + skyStatus);
                        break;
                    case "VEC":
                        System.out.println("풍향: " + value);
                        break;
                    case "WSD":
                        System.out.println("풍속: " + value + "m/s");
                        break;
                }
            });
            System.out.println("-----------------");

            // 파싱된 전체 JSON 데이터를 클라이언트에 반환
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("날씨 정보를 가져오는 중 오류가 발생했습니다.");
        }
    }
}