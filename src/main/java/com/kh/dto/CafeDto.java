package com.kh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CafeDto {
    private int cafeNo;
    private String cafeName;
    private String cafeBranch;
    private String cafeRegion;
    private String cafeImgAddress;
    private String cafeType;
    private String cafeOpen;
    private String cafePhonNumber;
    private String cafeAddress;
    private String cafeRating;
    private String cafeWebsite;
    private String cafeMapUrl;
}//