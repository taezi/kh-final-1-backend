package com.kh.service;

import com.kh.dto.Cafe;
import com.kh.dto.CafeDto;
import com.kh.mapper.CafeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CafeService {

    private final CafeMapper cafeMapper;
    private final GooglePlaceApiService googlePlaceApiService;

    public List<CafeDto> getCafesWithPhotos() {
        List<CafeDto> cafes = cafeMapper.findAllCafes();

        // 사진 URL이 없는 카페는 API 호출하여 데이터 채우기
        for (CafeDto cafe : cafes) {
            if (cafe.getCafeImgAddress() == null || cafe.getCafeImgAddress().isEmpty()) {
                Cafe detailedCafe = googlePlaceApiService.getCafeDetails(cafe.getCafeName(), cafe.getCafeBranch());
                if (detailedCafe != null) {
                    cafe.setCafeImgAddress(detailedCafe.getCafeImgAddress());
                    cafe.setCafeType(detailedCafe.getCafeType());
                    cafe.setCafeAddress(detailedCafe.getCafeAddress());
                    cafe.setCafeRating(detailedCafe.getCafeRating());
                    cafe.setCafeSummary(detailedCafe.getCafeSummary());
                    cafe.setCafePhonNumber(detailedCafe.getCagePhonNumber());
                    cafe.setCafeWebsite(detailedCafe.getCafeWebsite());
                    cafe.setCafeOpen(detailedCafe.getCafeOpen());
                    cafe.setCafeMapUrl(detailedCafe.getCafeMapUrl());
                    cafeMapper.updateCafeDetails(cafe);
                }
            }
        }
        return cafes.stream()
                .filter(cafe -> cafe.getCafeImgAddress() != null && !cafe.getCafeImgAddress().isEmpty())
                .collect(Collectors.toList());
    }

    public Cafe getCafeDetails(String cafeName) {
        return cafeMapper.findCafeByName(cafeName);
    }
}//