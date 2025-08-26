package com.kh.controller;

import com.kh.dto.RestDto;
import com.kh.service.RestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rests")
@RequiredArgsConstructor
public class RestrController {

    private final RestService restService;

    @GetMapping
    public List<RestDto> getRests() {
        return restService.getRestsWithPhotos();
    }

    @GetMapping("/{restName}")
    public RestDto getRestDetails(@PathVariable String restName) {
        return restService.getRestDetails(restName);
    }
}
