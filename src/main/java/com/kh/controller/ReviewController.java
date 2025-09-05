package com.kh.controller;

import com.kh.dto.ReviewDTO;
import com.kh.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // 1. 댓글 목록 조회
    @GetMapping("/{contentType}/{contentNo}")
    public ResponseEntity<List<ReviewDTO>> getComments(
            @PathVariable String contentType,
            @PathVariable Long contentNo
    ) {
        List<ReviewDTO> comments = reviewService.getComments(contentType, contentNo);
        return ResponseEntity.ok(comments);
    }

    // 2. 댓글 등록
    @PostMapping("/{contentType}/{contentNo}")
    public ResponseEntity<ReviewDTO> addComment(
            @PathVariable String contentType,
            @PathVariable Long contentNo,
            @RequestBody ReviewDTO comment
    ) {
        comment.setContenttype(contentType);
        comment.setContentno(contentNo);
        ReviewDTO saved = reviewService.addComment(comment);
        return ResponseEntity.ok(saved);
    }

    // 3. 댓글 수정
    @PutMapping("/{reviewNo}")
    public ResponseEntity<ReviewDTO> updateComment(
            @PathVariable Long reviewNo,
            @RequestBody ReviewDTO updatedComment
    ) {
        ReviewDTO updated = reviewService.updateComment(reviewNo, updatedComment.getCommenta());
        return ResponseEntity.ok(updated);
    }

    // 4. 댓글 삭제
    @DeleteMapping("/{reviewNo}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long reviewNo) {
        reviewService.deleteComment(reviewNo);
        return ResponseEntity.ok().build();
    }
}