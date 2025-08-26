package com.kh.controller;

import com.kh.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/now")
    public ResponseEntity<String> getSeoulWeather() {
        // 날씨 서비스에서 API 호출 결과를 받아 클라이언트에 반환
        System.out.println(weatherService.getSeoulWeather());
        return weatherService.getSeoulWeather();
    }
}
