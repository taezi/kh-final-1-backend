package com.kh.controller;

import com.kh.dto.CinemaDTO;
import com.kh.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    @GetMapping
    public List<CinemaDTO> getCinemasByRegion(@RequestParam("region") String region) {
        return cinemaService.searchCinemasByRegion(region);
    }
}