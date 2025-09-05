package com.kh.service;

import com.kh.dto.ReviewDTO;
import com.kh.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;


    // 댓글 목록 조회
    public List<ReviewDTO> getComments(String contentType, Long contentNo) {
        return reviewMapper.selectCommentsByContent(contentType, contentNo);
    }

    // 댓글 등록
    public ReviewDTO addComment(ReviewDTO comment) {
        reviewMapper.insertComment(comment);
        return comment;
    }

    // 댓글 수정
    public ReviewDTO updateComment(Long reviewNo, String commenta) {
        reviewMapper.updateComment(reviewNo, commenta);
        return reviewMapper.selectCommentById(reviewNo);
    }

    // 댓글 삭제
    public void deleteComment(Long reviewNo) {
        reviewMapper.deleteComment(reviewNo);
    }
}
