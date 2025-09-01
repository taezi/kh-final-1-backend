package com.kh.controller;

import com.kh.dto.EditorDTO;
import com.kh.service.EditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/editors") // 복수형으로 변경
public class EditorController {

    @Autowired
    private EditorService editorService;
    @Autowired
    private S3Presigner s3Presigner;
    private final String bucket = "kh-final-1";

    // 게시글 등록
    @PostMapping
    public ResponseEntity<?> createEditor(@RequestBody EditorDTO editorDTO) {
        editorService.insertEditor(editorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("저장 성공");
    }

    // 게시글 전체 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        Map<String, Object> map = new HashMap<>();
        List<EditorDTO> eList = editorService.selectEditorAll();
        System.out.println("eList : " + eList);
        map.put("eList", eList);
        return ResponseEntity.ok(map);
    }

    // 게시글 상세 조회
    @GetMapping("/{editorno}")
    public ResponseEntity<EditorDTO> detail(@PathVariable long editorno) {
        return ResponseEntity.ok(editorService.selectEditorById(editorno));
    }

    // 게시글 수정
    @PutMapping("/{editorno}")
    public ResponseEntity<?> updateEditor(@PathVariable Long editorno,
                                          @RequestBody EditorDTO editorDTO) {
        System.out.println("게시글 수정 글번호 : " + editorno);
        System.out.println("게시글 수정 정보 : " + editorDTO);
        editorDTO.setEditorno(editorno);
        boolean result = editorService.updateEditor(editorDTO);
        return result ? ResponseEntity.ok("수정 완료")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
    }

    // 게시글 삭제
    @DeleteMapping("/{editorno}")
    public ResponseEntity<?> deleteEditor(@PathVariable long editorno) {
        editorService.deleteEditor(editorno);
        return ResponseEntity.ok("삭제 완료");
    }

    // S3 Presigned URL 발급
    @GetMapping("/s3/presigned")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @RequestParam String filename,
            @RequestParam String contentType) {

        String key = "uploads/" + System.currentTimeMillis() + "_" + filename;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(r -> r
                        .signatureDuration(Duration.ofMinutes(1))
                        .putObjectRequest(objectRequest));

        URL presignedUrl = presignedRequest.url();

        Map<String, String> result = new HashMap<>();
        result.put("uploadUrl", presignedUrl.toString());
        result.put("fileUrl", "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + key);
        return ResponseEntity.ok(result);
    }
}
