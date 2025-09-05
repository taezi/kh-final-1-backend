package com.kh.mapper;

import com.kh.dto.ReviewDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {
    // 특정 콘텐츠의 댓글 목록 조회
    List<ReviewDTO> selectCommentsByContent(String contentType, Long contentNo);

    // 새로운 댓글 추가
    void insertComment(ReviewDTO comment);

    // 특정 댓글 단건 조회
    ReviewDTO selectCommentById(long reviewno);

    // 댓글 수정
    void updateComment(Long reviewNo, String commenta);

    // 댓글 삭제
    void deleteComment(Long reviewNo);
}
