package com.kh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestDto {
    private int restNo;
    private String restName;
    private String restBranch;
    private String restRegion;
    private String restImgAddress;
    private String restType;
    private String restOpen;
    private String restPhonNumber;
    private String restAddress;
    private String restRating;
    private String restWebsite;
    private String restMapUrl;
}
//