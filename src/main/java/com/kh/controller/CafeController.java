package com.kh.controller;

import com.kh.dto.Cafe;
import com.kh.dto.CafeDto;
import com.kh.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cafes")
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;

    @GetMapping
    public List<CafeDto> getCafes() {
        return cafeService.getCafesWithPhotos();
    }

    @GetMapping("/{cafeName}")
    public Cafe getCafeDetails(@PathVariable String cafeName) {
        return cafeService.getCafeDetails(cafeName);
    }
}
//