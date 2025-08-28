package com.kh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

//네이버 검색 API의 전체 응답 구조를 담음
@Getter
@Setter
public class NaverSearchResponse {
    @JsonProperty("items")
    private List<CinemaDTO> items;
}