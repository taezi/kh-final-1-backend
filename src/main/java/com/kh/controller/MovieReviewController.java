package com.kh.controller;

import com.kh.dto.MovieReviewDTO;
import com.kh.service.MovieReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie/review")
@RequiredArgsConstructor
public class MovieReviewController {
    private final MovieReviewService movieReviewService;

    // 특정 영화의 리뷰 목록을 조회하는 API
    // GET /api/movie/review/{contentNo}
    @GetMapping("/{contentNo}")
    public ResponseEntity<List<MovieReviewDTO>> getReviews(@PathVariable("contentNo") int contentNo) {
        System.out.println("### MovieReviewController: getReviews 메서드 진입.");
        System.out.println("### MovieReviewController: contentNo = " + contentNo);
        List<MovieReviewDTO> reviews = movieReviewService.getReviews(contentNo);
        System.out.println("### MovieReviewController: 조회된 리뷰 개수 = " + reviews.size());
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    // 새로운 영화 리뷰 작성하는 API
    // POST /api/movie/review
    @PostMapping
    public ResponseEntity<String> addReview(@RequestBody MovieReviewDTO review) {
        System.out.println("### MovieReviewController: addReview 메서드 진입.");
        System.out.println("### MovieReviewController: 수신된 리뷰 데이터: " + review);

        try {
            // SecurityContext에서 현재 인증된 사용자의 ID를 가져옴
            String userNoStr = SecurityContextHolder.getContext().getAuthentication().getName();
            int userNo = Integer.parseInt(userNoStr);

            // 가져온 사용자 ID를 DTO 객체에 설정
            review.setUserNo(userNo);

            System.out.println("### MovieReviewController: SecurityContext 인증 정보: " + SecurityContextHolder.getContext().getAuthentication().getName());
            System.out.println("### MovieReviewController: SecurityContext 권한 정보: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

            movieReviewService.addReview(review);
            return new ResponseEntity<>("리뷰가 성공적으로 등록되었습니다.", HttpStatus.CREATED);
        } catch (NumberFormatException e) {
            // 사용자 번호 변환 실패 시 (예: userNo가 숫자가 아닐 경우)
            System.err.println("### MovieReviewController: 사용자 번호 변환 실패 - " + e.getMessage());
            return new ResponseEntity<>("사용자 인증 정보가 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // 그 외 다른 예외 발생 시 (예: DB 오류)
            System.err.println("### MovieReviewController: 리뷰 등록 중 예기치 않은 오류 발생 - " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("리뷰 등록 중 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // 리뷰 수정 API 추가
    // PUT /api/movie/review/{reviewNo}
    @PutMapping("/{reviewNo}")
    public ResponseEntity<String> updateReview(@PathVariable("reviewNo") int reviewNo, @RequestBody MovieReviewDTO review) {
        try {
            String userNoStr = SecurityContextHolder.getContext().getAuthentication().getName();
            int userNo = Integer.parseInt(userNoStr);

            review.setReviewNo(reviewNo);
            review.setUserNo(userNo);

            movieReviewService.updateReview(review);
            return new ResponseEntity<>("리뷰가 성공적으로 수정되었습니다.", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("사용자 인증 정보가 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.err.println("리뷰 수정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("리뷰 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 리뷰 삭제 API 추가
    // DELETE /api/movie/review/{reviewNo}
    @DeleteMapping("/{reviewNo}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewNo") int reviewNo) {
        try {
            String userNoStr = SecurityContextHolder.getContext().getAuthentication().getName();
            int userNo = Integer.parseInt(userNoStr);

            movieReviewService.deleteReview(reviewNo, userNo);
            return new ResponseEntity<>("리뷰가 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("사용자 인증 정보가 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.err.println("리뷰 삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("리뷰 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

