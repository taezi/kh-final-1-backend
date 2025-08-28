package com.kh.mapper;

import com.kh.dto.NoticeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    // 공지글 등록
    void insertNotice(NoticeDTO noticeDTO);

    // 공지글 전체 조회 (페이징)
    List<NoticeDTO> selectNoticeAll(@Param("start") int start, @Param("end") int end);

    // 전체 공지글 수 조회 (페이지네이션용)
    long countNotice();

    // 공지글 상세 조회
    NoticeDTO selectNoticeById(long noticeno);

    // 공지글 수정
    int updateNotice(NoticeDTO noticeDTO);

    // 공지글 삭제
    void deleteNotice(long noticeno);


}
