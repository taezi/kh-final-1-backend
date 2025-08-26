package com.kh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto {
    private String placeName;       // 장소 이름
    private String photoUrl;        // 사진 URL
    private String placeType;       // 장소 유형
    private String address;         // 주소
    private Double rating;          // 평점
    private String summary;         // 간단한 소개
    private String phoneNumber;     // 전화번호
    private String website;         // 웹사이트 URL
    private String openingHours;    // 영업시간 정보
    private String mapUrl;          // 지도 URL
}