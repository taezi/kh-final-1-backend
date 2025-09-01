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
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 공지사항 등록
    @PostMapping
    public ResponseEntity<?> createNotice(@RequestBody NoticeDTO noticeDTO) {
        System.out.println("공지사항 받은 데이터: " + noticeDTO);
        noticeService.insertNotice(noticeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("저장 성공");
    }

    // 공지사항 전체 목록 조회 (페이징 가능)
    @GetMapping({"", "/"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        System.out.println("page : " + page);
        Map<String, Object> map = new HashMap<>();


        List<NoticeDTO> nList = noticeService.selectNoticeAll(page, size);
        long total = noticeService.countNotice(); // 전체 게시글 수

        map.put("nList", nList);
        map.put("total", total);
        System.out.println("nList : " + nList);
        System.out.println("total : " + total);
        return ResponseEntity.ok(map);
    }

    // 공지사항 상세 조회
    @GetMapping("/{noticeno}")
    public NoticeDTO detail(@PathVariable long noticeno) {
        return noticeService.selectNoticeById(noticeno);
    }

    // 공지사항 수정
    @PutMapping("/{noticeno}")
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
    @DeleteMapping("/{noticeno}")
    public ResponseEntity<?> deleteNotice(@PathVariable long noticeno) {
        noticeService.deleteNotice(noticeno);
        return ResponseEntity.ok("삭제 완료");
    }
}
