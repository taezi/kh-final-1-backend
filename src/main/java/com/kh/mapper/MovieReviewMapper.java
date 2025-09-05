package com.kh.mapper;

import com.kh.dto.MovieReviewDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MovieReviewMapper {
    // 특정 콘텐츠에 대한 리뷰 목록을 가져오는 메서드
    List<MovieReviewDTO> getReviewsByContent(int contentNo);

    // 새로운 리뷰를 데이터베이스에 삽입하는 메서드
    void insertReview(MovieReviewDTO movieReview);

    int updateReview(MovieReviewDTO review);
    void deleteReview(int reviewNo, int userNo);
}
