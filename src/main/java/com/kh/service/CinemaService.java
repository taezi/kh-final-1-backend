package com.kh.service;

import com.kh.dto.CinemaDTO;
import com.kh.dto.KakaoSearchResponse;
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

    @Value("${kakao.api.rest-api-key}")
    private String kakaoRestApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String KAKAO_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    public List<CinemaDTO> searchCinemasByRegion(String region) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(KAKAO_SEARCH_URL)
                    .queryParam("query", region + " 영화관")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<KakaoSearchResponse> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, KakaoSearchResponse.class);

            System.out.println("카카오 API 응답: " + response.getBody());

            return response.getBody() != null ? response.getBody().getDocuments() : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}