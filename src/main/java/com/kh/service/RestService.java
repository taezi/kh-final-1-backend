package com.kh.service;

import com.kh.dto.RestDto;
import com.kh.mapper.RestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestService {

    private final RestMapper restMapper;
    private final GooglePlaceApiService googlePlaceApiService;

    public List<RestDto> getRestsWithPhotos() {
        List<RestDto> rests = restMapper.findAllRests();

        // 사진 URL이 없는 식당은 API 호출하여 데이터 채우기
        for (RestDto rest : rests) {
            if (rest.getRestImgAddress() == null || rest.getRestImgAddress().isEmpty()) {
                RestDto detailedRest = googlePlaceApiService.getRestDetails(rest.getRestName(),rest.getRestBranch());
                if (detailedRest != null) {
                    rest.setRestImgAddress(detailedRest.getRestImgAddress());
                    rest.setRestType(detailedRest.getRestType());
                    rest.setRestAddress(detailedRest.getRestAddress());
                    rest.setRestRating(detailedRest.getRestRating());
                    rest.setRestSummary(detailedRest.getRestSummary());
                    rest.setRestPhonNumber(detailedRest.getRestPhonNumber());
                    rest.setRestWebsite(detailedRest.getRestWebsite());
                    rest.setRestOpen(detailedRest.getRestOpen());
                    rest.setRestMapUrl(detailedRest.getRestMapUrl());
                    restMapper.updateRestDetails(rest);
                }
            }
        }
        return rests.stream()
                .filter(rest -> rest.getRestImgAddress() != null && !rest.getRestImgAddress().isEmpty())
                .collect(Collectors.toList());
    }

    public RestDto getRestDetails(String restName) {
        return restMapper.findRestByName(restName);
    }
}