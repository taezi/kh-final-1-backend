package com.kh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.dto.EditorDTO;
import com.kh.dto.HashtagDTO;
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
    public Map<String, Object> createEditor(@RequestBody EditorDTO editorDTO) {
        Map<String, Object> map = new HashMap<>();
        editorService.insertEditor(editorDTO);
        int maxEditorno = editorService.selectMaxEditorno();
        map.put("editorno", maxEditorno);
        return map;
    }

    // 게시글 전체 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        Map<String, Object> map = new HashMap<>();
        List<EditorDTO> eList = editorService.selectEditorAll();
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

    // 게시글 검색 기능
    @GetMapping("/editor")
    public ResponseEntity<Map<String, Object>> getEditors(
            @RequestParam(required = false) String search) {

        List<EditorDTO> editors = editorService.findEditors(search);

        Map<String, Object> result = new HashMap<>();
        result.put("eList", editors);

        return ResponseEntity.ok(result);
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

    @PostMapping("/hashtag")
    public void saveEditorHashtags(@RequestBody Map<String, Object> request){

        Long editorno = Long.parseLong(request.get("editorno").toString());
        List<String> hashtags = (List<String>) request.get("hashtags");

        System.out.println("받은 데이터: " + request);
        System.out.println("게시글 번호: " + editorno);
        System.out.println("해시태그 목록: " + hashtags);

        for (String tagName : hashtags) {
            Long hashtagId = editorService.findHashtagIdByName(tagName);
            System.out.println("tagName : " + tagName);
            System.out.println("hashtagId : " + hashtagId);

            if (hashtagId == null) {
                HashtagDTO hashtag = new HashtagDTO();
                hashtag.setTagname(tagName);
                editorService.insertHashtag(hashtag);
                hashtagId = hashtag.getHashtagid();
            }
            System.out.println("hashtagId : " + hashtagId);
            Map<String, Long> map = new HashMap<>();
            map.put("editorno", editorno);
            map.put("hashtagid", hashtagId);
            editorService.insertEditorHashtag(map);
        }
    }

    // 게시글 + 해시태그 수정
    @PutMapping("/{editorno}/with-hashtags")
    public ResponseEntity<?> updateEditorWithHashtags(
            @PathVariable Long editorno,
            @RequestBody Map<String, Object> request
    ) {
        System.out.println(editorno);
        System.out.println(request);
        try {
            // 게시글 정보 업데이트
            EditorDTO editorDTO = new ObjectMapper().convertValue(request.get("editor"), EditorDTO.class);
            editorDTO.setEditorno(editorno);
            editorService.updateEditor(editorDTO);

            // 해시태그 갱신
            List<String> hashtags = (List<String>) request.get("hashtags");
            editorService.updateEditorHashtags(editorno, hashtags);

            return ResponseEntity.ok("수정 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
        }
    }


}
