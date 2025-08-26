package com.kh.service;

import com.kh.dto.SeoulguDTO;
import com.kh.mapper.SeoulguMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String serviceKey;

    @Value("${weather.location.seoul.nx}")
    private int nx;

    @Value("${weather.location.seoul.ny}")
    private int ny;

    private final SeoulguMapper seoulguMapper;




    // 기상청 base_time은 정해진 8개 값 중 직전 시간 사용
    private String getBaseTime() {
        int[] baseTimes = {2300, 2000, 1700, 1400, 1100, 800, 500, 200};

        // 현재 시각
        int now = LocalTime.now().getHour() * 100 + LocalTime.now().getMinute();

        for (int bt : baseTimes) {
            if (now >= bt) {
                return String.format("%04d", bt);
            }
        }
        // 새벽 0시~2시 사이는 전날 23:00 사용
        return "2300";
    }

    public String getSeoulWeather() {
        try {
            // 오늘 날짜
            String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = getBaseTime();

            String apiURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?";
            apiURL += "serviceKey=" + serviceKey;
            apiURL += "&numOfRows=100";
            apiURL += "&pageNo=1";
            apiURL += "&dataType=JSON";
            apiURL += "&base_date=" + baseDate;
            apiURL += "&base_time=" + baseTime;
            apiURL += "&nx=" + nx;
            apiURL += "&ny=" + ny;
            System.out.println("url : " + apiURL);

            // 1. 요청
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");


            if (conn.getResponseCode() != 200) {
                return "API 호출 실패: " + conn.getResponseCode();
            }

            // 2. 응답 읽기
            StringBuilder result = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
            }
            conn.disconnect();

            // 3. JSON 파싱
            JSONObject json = new JSONObject(result.toString());
            JSONArray arr = json.getJSONObject("response")
                    .getJSONObject("body")
                    .getJSONObject("items")
                    .getJSONArray("item");

            JSONObject weather = new JSONObject();

            arr.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                switch (obj.getString("category")) {
                    case "TMP": // 기온
                        weather.put("temp", obj.getString("fcstValue"));
                        break;
                    case "SKY": // 하늘 상태
                        weather.put("sky", obj.getString("fcstValue"));
                        break;
                    case "PTY": // 강수 형태
                        weather.put("pty", obj.getString("fcstValue"));
                        break;
                }
            });

            weather.put("baseDate", baseDate);
            weather.put("baseTime", baseTime);
            System.out.println(weather.toString());

            return weather.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Exception 발생\"}";
        }
    }


    public String getFutureWeather(String gu) {
        try {
            String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = getBaseTime();

            SeoulguDTO seoul = seoulguMapper.findseoul(gu);

            System.out.println("data : " + seoul);


            int nx = seoul.getNx();
            int ny = seoul.getNy();

            String apiURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?"
                    + "serviceKey=" + serviceKey
                    + "&numOfRows=1000"
                    + "&pageNo=1"
                    + "&dataType=JSON"
                    + "&base_date=" + baseDate
                    + "&base_time=" + "0800"
                    + "&nx=" + nx
                    + "&ny=" + ny;

            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return new JSONObject()
                        .put("error", "API 호출 실패")
                        .put("code", conn.getResponseCode())
                        .toString();
            }

            StringBuilder result = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) result.append(line);
            }
            conn.disconnect();

            JSONObject json = new JSONObject(result.toString());
            JSONArray arr = json.getJSONObject("response")
                    .getJSONObject("body")
                    .getJSONObject("items")
                    .getJSONArray("item");

            // 시각별(TMP/SKY/PTY) 합치기
            Map<String, JSONObject> slotMap = new HashMap<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject it = arr.getJSONObject(i);
                String cat = it.getString("category");
                if (!("TMP".equals(cat) || "SKY".equals(cat) || "PTY".equals(cat))) continue;

                String date = it.getString("fcstDate"); // yyyymmdd
                String time = it.getString("fcstTime"); // HHmm
                String key = date + time;

                JSONObject slot = slotMap.getOrDefault(key, new JSONObject()
                        .put("fcstDate", date)
                        .put("fcstTime", time));
                String val = it.getString("fcstValue");

                switch (cat) {
                    case "TMP": slot.put("temp", val); break;
                    case "SKY": slot.put("sky", val);  break;
                    case "PTY": slot.put("pty", val);  break;
                }
                slotMap.put(key, slot);
            }

            // "현재" = 예보 시각 중 현재시각과 가장 가까운 것 선택
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter D = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            JSONObject current = null;
            long bestDiff = Long.MAX_VALUE;

            for (JSONObject s : slotMap.values()) {
                String dtKey = s.getString("fcstDate") + s.getString("fcstTime");
                LocalDateTime t = LocalDateTime.of(
                        LocalDate.parse(dtKey.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd")),
                        LocalTime.parse(dtKey.substring(8), DateTimeFormatter.ofPattern("HHmm"))
                );
                long diff = Math.abs(Duration.between(now, t).toMinutes());
                if (diff < bestDiff) {
                    bestDiff = diff;
                    current = s;
                }
            }

            // 오늘/내일/모레 09:00 / 15:00
            LocalDate today = LocalDate.now();
            JSONArray days = new JSONArray();
            for (int i = 0; i < 3; i++) {
                LocalDate target = today.plusDays(i);
                String ds = target.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                JSONObject am = slotMap.getOrDefault(ds + "0900", new JSONObject());
                JSONObject pm = slotMap.getOrDefault(ds + "1500", new JSONObject());

                JSONObject day = new JSONObject()
                        .put("date", ds)
                        .put("label", (i == 0 ? "오늘" : (i == 1 ? "내일" : "모레")))
                        .put("am", am)
                        .put("pm", pm);
                days.put(day);
            }

            // 결과 JSON
            JSONObject out = new JSONObject();
            out.put("gu", gu);
            out.put("baseDate", baseDate);
            out.put("baseTime", baseTime);
            out.put("current", current != null ? current : new JSONObject());
            out.put("days", days);
            out.put("pm10", JSONObject.NULL); // 미세먼지 자리 비워둠

            return out.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", "Exception 발생").toString();
        }
    }






}
