package com.kh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Place {
    private String placeName;       // DB의 place_name 컬럼과 매핑
    private String photoUrl;        // DB의 photo_url 컬럼과 매핑
    private String placeType;       // DB의 place_type 컬럼과 매핑
    private String address;         // DB의 address 컬럼과 매핑
    private Double rating;          // DB의 rating 컬럼과 매핑
    private String summary;         // DB의 summary 컬럼과 매핑
    private String phoneNumber;     // DB의 phone_number 컬럼과 매핑
    private String website;         // DB의 website 컬럼과 매핑
    private String openingHours;    // DB의 opening_hours 컬럼과 매핑
    private String mapUrl;          // DB의 map_url 컬럼과 매핑
}