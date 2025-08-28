package com.kh.controller;

import com.kh.dto.NoticeDTO;
import com.kh.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 공지사항 등록
    @PostMapping("/write")
    public String createNotice(@RequestBody NoticeDTO noticeDTO) {
        System.out.println("공지사항 받은 데이터: " + noticeDTO);
        noticeService.insertNotice(noticeDTO);
        return "저장 성공";
    }

    // 공지사항 전체 목록 조회 (페이징 가능)
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> map = new HashMap<>();

        /*int offset = (page - 1) * size;*/ // 이걸 왜여따 넣음?? 이거 오류

        /*
        * page :1
        * size :10
        *  파라미터로 넘기는데  offset 미리계산해서
        * noticeService.selectNoticeAll 로직에서 start end 파라미터 잘못 넘어가서 오류가남
        * noticeService.selectNoticeAll(offset, @RequestParam(defaultValue = "10") int size) 비정상 offset 으로 인한 파라미터 값오류
        * noticeService.selectNoticeAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) 정상
        *  이상
        *   */



        List<NoticeDTO> nList = noticeService.selectNoticeAll(page, size);
        long total = noticeService.countNotice(); // 전체 게시글 수

        map.put("nList", nList);
        map.put("total", total);
        return map;
    }

    // 공지사항 상세 조회
    @GetMapping("/detail/{noticeno}")
    public NoticeDTO detail(@PathVariable long noticeno) {
        return noticeService.selectNoticeById(noticeno);
    }

    // 공지사항 수정
    @PutMapping("/update/{noticeno}")
    public ResponseEntity<?> updateNotice(@PathVariable long noticeno,
                                          @RequestBody NoticeDTO noticeDTO) {
        noticeDTO.setNoticeno(noticeno); // path variable 적용
        boolean result = noticeService.updateNotice(noticeDTO);

        if (result) {
            return ResponseEntity.ok("수정 완료");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
        }
    }

    // 공지사항 삭제
    @DeleteMapping("/delete/{noticeno}")
    public String deleteNotice(@PathVariable long noticeno) {
        noticeService.deleteNotice(noticeno);
        return "삭제 완료";
    }
}
