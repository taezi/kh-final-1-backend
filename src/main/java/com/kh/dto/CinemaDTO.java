package com.kh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CinemaDTO {
    @JsonProperty("place_name")
    private String name; // 카카오 API의 영화관 이름 필드명
    @JsonProperty("address_name")
    private String address; // 카카오 API의 주소 필드명
    @JsonProperty("x")
    private String x; // 경도 (longitude)
    @JsonProperty("y")
    private String y; // 위도 (latitude)
}