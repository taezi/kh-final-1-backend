package com.kh.controller;

import com.kh.service.WeatherService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/weather")
@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/now")
    public String getSeoulWeather() {
        return weatherService.getSeoulWeather();
    }
    @GetMapping(value = "/future", produces = MediaType.APPLICATION_JSON_VALUE)
    public String future(@RequestParam(defaultValue = "강남구") String gu) {
        return weatherService.getFutureWeather(gu);
    }
}