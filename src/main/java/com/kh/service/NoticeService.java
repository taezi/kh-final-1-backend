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

    //공지글 저장
    public void insertNotice(NoticeDTO noticeDTO) {
        noticeMapper.insertNotice(noticeDTO);
    }
    // 공지글 전체 조회 (페이징)
    public List<NoticeDTO> selectNoticeAll(int page, int size) {
        int start = (page - 1) * size + 1;
        int end = page * size;
        return noticeMapper.selectNoticeAll(start, end);
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
        int result = noticeMapper.updateNotice(noticeDTO);
        return result > 0;
    }

    // 공지글 삭제
    public void deleteNotice(long noticeno) {
        noticeMapper.deleteNotice(noticeno);
    }

    public boolean incrementView(long noticeno) {
        int update = noticeMapper.incrementView(noticeno);
        return update > 0;
    }
}




