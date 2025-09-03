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
}
