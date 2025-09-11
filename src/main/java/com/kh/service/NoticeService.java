package com.kh.service;

import com.kh.dto.NoticeDTO;
import com.kh.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    // 공지글 저장
    public void insertNotice(NoticeDTO noticeDTO) {
        noticeMapper.insertNotice(noticeDTO);
    }

    // 공지글 전체 조회 (페이징) - offset/limit 계산
    public List<NoticeDTO> selectNoticeAll(int page, int size) {
        int offset = Math.max(0, (page - 1) * size);
        int limit = size;
        return noticeMapper.selectNoticeAll(offset, limit);
    }

    // 전체 게시글 수
    public long countNotice() {
        return noticeMapper.countNotice();
    }

    // 공지글 상세 조회
    public NoticeDTO selectNoticeById(long noticeno) {
        return noticeMapper.selectNoticeById(noticeno);
    }

    // 공지글 수정
    public boolean updateNotice(NoticeDTO noticeDTO) {
        return noticeMapper.updateNotice(noticeDTO) > 0;
    }

    // 공지글 삭제
    public void deleteNotice(long noticeno) {
        noticeMapper.deleteNotice(noticeno);
    }

    // 조회수 증가
    public boolean incrementView(long noticeno) {
        return noticeMapper.incrementView(noticeno) > 0;
    }
}
