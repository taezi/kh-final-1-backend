package com.kh.controller;

import com.kh.service.EditorService;
import com.kh.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/views")
public class ViewsController {

    @Autowired
    private EditorService editorService;

    @Autowired
    private  NoticeService noticeService;


    // 조회수 증가
    @PutMapping("/notice/{noticeno}")
    public ResponseEntity<?> incrementNoticeView(@PathVariable long noticeno) {
        boolean result = noticeService.incrementView(noticeno);

        return result
                ? ResponseEntity.ok("조회수 증가 완료")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("조회수 증가 실패");
    }

    // 조회수 증가
    @PutMapping("/editor/{editorno}")
    public ResponseEntity<?> incrementEditorView(@PathVariable long editorno) {
        boolean result = editorService.incrementView(editorno);
        return result
                ? ResponseEntity.ok("조회수 증가 완료")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("조회수 증가 실패");
    }

}
