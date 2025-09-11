package com.kh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("moviereview")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieReviewDTO {
    private int reviewNo; //리뷰 고유 번호
    private int userNo; // 유저 고유 번호
    private String username;
    private String commentA;
    private String createDat;
    private String contentType; // 콘텐츠 타입
    private int contentNo; // 콘텐츠 고유 번호
}
