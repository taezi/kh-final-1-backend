package com.kh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

//영화관 한 곳의 정보. 네이버 검색 API의 응답 필드명과 일치시켜야 함
@Getter
@Setter
public class CinemaDTO {
    @JsonProperty("title")
    private String title;
    @JsonProperty("address")
    private String address;
    @JsonProperty("mapx")
    private String mapX;
    @JsonProperty("mapy")
    private String mapY;
}