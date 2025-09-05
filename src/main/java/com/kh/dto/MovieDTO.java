package com.kh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
//개별 영화 한 편의 정보를 담는 DTO
@Getter
@Setter
public class MovieDTO {
    private String title;
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("release_date")
    private String releaseDate;
}