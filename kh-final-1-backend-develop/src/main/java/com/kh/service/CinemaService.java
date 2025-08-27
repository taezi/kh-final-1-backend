package com.kh.service;

import com.kh.dto.CinemaDTO;
import com.kh.dto.NaverSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Service
public class CinemaService {

    @Value("${naver.api.client-id}")
    private String naverClientId;
    @Value("${naver.api.client-secret}")
    private String naverClientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String NAVER_SEARCH_URL = "https://openapi.naver.com/v1/search/local.json";

    public List<CinemaDTO> searchCinemasByRegion(String region) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(NAVER_SEARCH_URL)
                    .queryParam("query", region + " 영화관")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", naverClientId);
            headers.set("X-Naver-Client-Secret", naverClientSecret);

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<NaverSearchResponse> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, NaverSearchResponse.class);

            System.out.println("네이버 API 응답: " + response.getBody());

            return response.getBody() != null ? response.getBody().getItems() : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}