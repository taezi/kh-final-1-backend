package com.kh.controller;

import com.kh.service.GooglePlaceApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class MapController {
    private final GooglePlaceApiService googlePlaceApiService;

    /** 주소/장소 배열 받아서 Directions 임베드 URL 반환 */
    @PostMapping("/directions-url")
    public Mono<ResponseEntity<Map<String, String>>> directionsUrl(@RequestBody Map<String, List<String>> body) {
        List<String> addresses = body.get("addresses");
        System.out.println("address" + addresses);
        System.out.println("추가추가");
        System.out.println("adadasdadad : " + googlePlaceApiService
                .buildDirectionsEmbedUrlByQueries(addresses) // ← 서비스에 추가한 메서드 재활용
                .map(url -> ResponseEntity.ok(Map.of("url", url))));
        return googlePlaceApiService
                .buildDirectionsEmbedUrlByQueries(addresses) // ← 서비스에 추가한 메서드 재활용
                .map(url -> ResponseEntity.ok(Map.of("url", url)));
    }

}