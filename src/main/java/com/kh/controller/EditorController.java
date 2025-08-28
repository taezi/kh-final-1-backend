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
@RequestMapping("/api/editor")
public class EditorController {

    @Autowired
    private EditorService editorService;
    @Autowired
    private S3Presigner s3Presigner;
    private final String bucket = "kh-final-1";



    //게시글 등록
    @PostMapping("/posts")
    public String createPost(@RequestBody EditorDTO editorDTO) {
        System.out.println("받은 데이터: " + editorDTO);
        System.out.println(editorDTO.getEditorcontent());
//        String content = editorDTO.getEditorcontent();
//
//        editorDTO.setEditortitle(content);

        editorService.insertEditor(editorDTO);
        return "저장 성공";
    }

    //게시글 전체 목록 조회
    @GetMapping("/list")
    public  Map<String, Object> list(){
        Map<String, Object> map = new HashMap<>();
        List<EditorDTO> elist = editorService.selectEditorAll();
        map.put("eList",elist);
        return map;
    }

    //게시글 상세 조회
    @GetMapping("/detail/{editorno}")
    public EditorDTO detail(@PathVariable long editorno) {
        return editorService.selectEditorById(editorno);
    }

    //게시글 수정버튼
    @PutMapping("/update/{editorno}")
    public ResponseEntity<?> updateEditor(@PathVariable Long editorno,
                                          @RequestBody EditorDTO editorDTO) {
        editorDTO.setEditorno(editorno); // path variable 적용
        boolean result = editorService.updateEditor(editorDTO);

        if (result) {
            return ResponseEntity.ok("수정 완료");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
        }
    }

    //삭제
    @DeleteMapping("/delete/{editorno}")
    public String deletePost(@PathVariable long editorno) {
        editorService.deleteEditor(editorno);
        return "삭제 완료";
    }
    // s3이미지 업로드
    @GetMapping("/s3/presigned")
    public Map<String, String> getPresignedUrl(
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
                        .signatureDuration(Duration.ofMinutes(1)) // URL 유효시간 1분
                        .putObjectRequest(objectRequest));

        URL presignedUrl = presignedRequest.url();

        Map<String, String> result = new HashMap<>();
        result.put("uploadUrl", presignedUrl.toString()); // PUT할 주소
        result.put("fileUrl", "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + key); // 업로드 완료 후 접근 URL
        System.out.println(result);
        return result;
    }

}
