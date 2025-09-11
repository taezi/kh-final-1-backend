package com.kh.mapper;

import com.kh.dto.NoticeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    // 공지글 등록
    void insertNotice(NoticeDTO noticeDTO);

    // 공지글 전체 조회 (페이징) - offset/limit 방식
    List<NoticeDTO> selectNoticeAll(@Param("offset") int offset,
                                    @Param("limit") int limit);

    // 전체 공지글 수
    long countNotice();

    // 공지글 상세 조회
    NoticeDTO selectNoticeById(long noticeno);

    // 공지글 수정
    int updateNotice(NoticeDTO noticeDTO);

    // 공지글 삭제
    void deleteNotice(long noticeno);

    // 조회수 증가
    int incrementView(long noticeno);
}
