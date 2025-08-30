package com.kh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KakaoSearchResponse {
    @JsonProperty("documents")
    private List<CinemaDTO> documents;
}