package com.kh.service;

import com.kh.dto.MovieReviewDTO;
import com.kh.mapper.MovieReviewMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieReviewService {
    private final MovieReviewMapper movieReviewMapper;

    // 특정 콘텐츠의 모든 리뷰를 가져옴
    public List<MovieReviewDTO> getReviews(int contentNo) {
        return movieReviewMapper.getReviewsByContent(contentNo);
    }

    // 새로운 리뷰를 저장
    @Transactional
    public void addReview(MovieReviewDTO movieReview) {
        movieReviewMapper.insertReview(movieReview);
    }

    // 리뷰 수정 메서드
    @Transactional
    public void updateReview(MovieReviewDTO review) {
        int updatedRows = movieReviewMapper.updateReview(review);

        // 3. 만약 업데이트된 행이 0이라면 예외를 발생시켜 클라이언트에 에러를 알림
        if (updatedRows == 0) {
            throw new RuntimeException("리뷰 업데이트에 실패했습니다. 리뷰가 존재하지 않거나 권한이 없습니다.");
        }
    }

    // 리뷰 삭제 메서드
    @Transactional
    public void deleteReview(int reviewNo, int userNo) {
        movieReviewMapper.deleteReview(reviewNo, userNo);
    }


}
