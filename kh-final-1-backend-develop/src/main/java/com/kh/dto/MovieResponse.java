package com.kh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovieResponse {
    private int page; //현재 페이지 번호를 담는 필드
    private List<MovieDTO> results;  //영화 목록 배열 담는 필드
    @JsonProperty("total_pages")
    private int totalPages;  //총 페이지 수를 담는 필드
    @JsonProperty("total_results")
    private int totalResults;  //검색 결과로 나온 총 영화 수를 담는 필드
}