package com.kh.controller;

import com.kh.dto.CafeDto;
import com.kh.service.CafeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * CafeController 클래스는 클라이언트의 HTTP 요청을 처리하는 컨트롤러 계층입니다.
 * CafeService를 통해 비즈니스 로직을 수행합니다.
 */
@RestController
@RequestMapping("/api/cafe")
public class CafeController {

    @Autowired
    private CafeService cafeService;

    /**
     * 카페 이름과 지점명으로 카페 정보를 조회하는 API 엔드포인트입니다.
     * 예: GET /api/cafe?cafeName=스타벅스&cafeBranch=강남점
     *
     * @param cafeName 조회할 카페 이름
     * @param cafeBranch 조회할 카페 지점명
     * @param headers 클라이언트가 보낸 모든 HTTP 헤더. Postman의 인증 헤더를 받기 위해 추가되었습니다.
     * @return 조회된 카페 정보 또는 HTTP 404 Not Found
     */
    @GetMapping
    public ResponseEntity<CafeDto> getCafeInfo(
            @RequestParam String cafeName,
            @RequestParam String cafeBranch,
            @RequestHeader Map<String, String> headers // 모든 헤더를 맵으로 받음
    ) throws UnsupportedEncodingException {

        // 클라이언트에서 보낸 헤더들을 로깅하여 확인 (디버깅용)
        // headers.forEach((key, value) -> System.out.println("Header: " + key + " = " + value));

        CafeDto cafe = cafeService.getCafeByNameAndBranch(cafeName, cafeBranch);
        if (cafe != null) {
            return ResponseEntity.ok(cafe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 기존 카페 정보를 업데이트하는 API 엔드포인트입니다.
     * 예: POST /api/cafe/update
     * 요청 본문으로 CafeDto 객체를 받습니다.
     * @param cafeDto 업데이트할 정보가 담긴 CafeDto 객체
     * @return 업데이트 성공 여부
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateCafe(@RequestBody CafeDto cafeDto) {
        boolean success = cafeService.updateCafe(cafeDto);
        if (success) {
            return ResponseEntity.ok("카페 정보가 성공적으로 업데이트되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("카페 정보 업데이트에 실패했습니다. (cafeNo 확인)");
        }
    }
}