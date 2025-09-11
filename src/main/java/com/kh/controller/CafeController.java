package com.kh.controller;

import com.kh.dto.CafeDto;
import com.kh.service.CafeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * CafeController 클래스는 클라이언트의 HTTP 요청을 처리하는 컨트롤러 계층입니다.
 * CafeService를 통해 비즈니스 로직을 수행합니다.
 */
@RestController
@RequestMapping("/api/cafe")
@CrossOrigin(origins = "http://localhost:3000")
public class CafeController {

    @Autowired
    private CafeService cafeService;

    /**
     * 카페 번호로 카페 정보를 조회하는 API 엔드포인트입니다.
     * cafeNo가 제공되지 않으면 cafeName과 cafeBranch를 사용합니다.
     * @param cafeNo 조회할 카페 번호 (선택적)
     * @param cafeName 조회할 카페 이름 (선택적)
     * @param cafeBranch 조회할 카페 지점명 (선택적)
     * @return 조회된 카페 정보 또는 HTTP 404 Not Found
     */
    @GetMapping("/info")
    public ResponseEntity<CafeDto> getCafeInfo(
            @RequestParam(required = false) Integer cafeNo,
            @RequestParam(required = false) String cafeName,
            @RequestParam(required = false) String cafeBranch
    ) throws UnsupportedEncodingException {

        CafeDto cafe = null;
        if (cafeNo != null) {
            // cafeNo가 있으면 cafeNo로 조회
            cafe = cafeService.getCafeByNo(cafeNo);
        } else if (cafeName != null && cafeBranch != null) {
            // cafeNo가 없으면 cafeName과 cafeBranch로 조회
            cafe = cafeService.getCafeByNameAndBranch(cafeName, cafeBranch);
        } else {
            // 필수 파라미터가 누락된 경우
            return ResponseEntity.badRequest().build();
        }

        if (cafe != null) {
            return ResponseEntity.ok(cafe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    /**
     * 기존 식당 정보를 업데이트하는 API 엔드포인트입니다.
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

    /**
     * 특정 지역구(gu)와 검색어(q)를 사용하여 식당 목록을 조회하는 API 엔드포인트입니다.
     * 페이징을 위해 페이지 번호(page)와 페이지당 항목 수(size)를 받습니다.
     * 예: GET /api/cafe/search?gu=강남구&q=카페&page=1&size=12
     * @param gu 조회할 지역구 이름 (선택적)
     * @param q 조회할 검색어 (선택적)
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 12)
     * @return 조회된 식당 목록과 페이징 정보가 담긴 응답
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCafeaurants(
            @RequestParam(value = "gu", required = false) String gu,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "12") int size) {

        // cafeService에서 페이징된 데이터를 가져옵니다.
        Map<String, Object> result = cafeService.searchCafes(gu, q, page, size);

        // 결과가 비어있지 않으면 200 OK와 함께 데이터를 반환합니다.
        if (result != null && !((List<CafeDto>) result.get("items")).isEmpty()) {
            return ResponseEntity.ok(result);
        } else {
            // 결과가 없으면 빈 리스트와 hasMore를 false로 반환하여 프론트엔드에서 처리할 수 있게 합니다.
            result.put("items", List.of());
            result.put("hasMore", false);
            return ResponseEntity.ok(result);
        }
    }
}