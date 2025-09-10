package com.kh.service;

import com.kh.dto.MovieReviewDTO;
import com.kh.mapper.MovieReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieReviewService {
    private final MovieReviewMapper movieReviewMapper;

    // 특정 콘텐츠의 모든 리뷰를 가져옴
    public List<MovieReviewDTO> getReviews(int contentNo) {
        return movieReviewMapper.getReviewsByContent(contentNo);
    }

    // ⭐ 새로운 리뷰를 저장하는 통합 메서드 (사진 기능 제외)
    @Transactional
    public void addReview(MovieReviewDTO movieReview) {
        // review_seq.nextval을 사용하여 reviewNo를 먼저 가져와 DTO에 설정합니다.
        int reviewNo = movieReviewMapper.getNextReviewNo();
        movieReview.setReviewNo(reviewNo);

        // REVIEW 테이블에 데이터 삽입
        movieReviewMapper.insertReview(movieReview);
    }

    // 리뷰 수정 메서드
    @Transactional
    public void updateReview(MovieReviewDTO review) {
        int updatedRows = movieReviewMapper.updateReview(review);

        if (updatedRows == 0) {
            throw new RuntimeException("리뷰 업데이트에 실패했습니다. 리뷰가 존재하지 않거나 권한이 없습니다.");
        }
    }

    // 리뷰 삭제 메서드
    @Transactional
    public void deleteReview(int reviewNo, int userNo) {
        // REVIEW_FILE 삭제 로직은 더 이상 필요 없으므로 제거
        // movieReviewMapper.deleteReviewFile(reviewNo);
        movieReviewMapper.deleteReview(reviewNo, userNo);
    }
}