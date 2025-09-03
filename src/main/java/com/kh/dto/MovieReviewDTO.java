package com.kh.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class MovieReviewDTO {
    private int reviewNo; // REVIEWNO (리뷰 고유 번호)
    private int userNo; // USERNO (유저 고유 번호)
    @Column(name = "COMMENTA") // 데이터베이스 컬럼명과 매핑
    private String comment; // 필드명을 'comment'로 변경
    private String createDat; // CREATEDAT (작성일)
    private String contentType; // CONTENTTYPE (콘텐츠 타입)
    private int contentNo; // CONTENTNO (콘텐츠 고유 번호)
}
