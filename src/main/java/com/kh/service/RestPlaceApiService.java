package com.kh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.dto.RestDto; // RestDto 클래스를 import 합니다.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Rest Places API를 사용하여 카페 정보를 조회하고 DTO로 변환하는 서비스입니다.
 * WebClient를 사용하여 논블로킹 방식으로 API를 호출합니다.
 */
@Service
public class RestPlaceApiService {

    // 로거 설정
    private static final Logger logger = LoggerFactory.getLogger(RestPlaceApiService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // application.properties에서 Rest Places API 키 주입
    @Value("${google.places.api-key}")
    private String googlePlacesApiKey;

    // Rest Places API의 엔드포인트 URL
    private static final String FIND_PLACE_API_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json";
    private static final String PLACE_DETAILS_API_URL = "https://maps.googleapis.com/maps/api/place/details/json";
    private static final String PLACE_PHOTO_API_URL = "https://maps.googleapis.com/maps/api/place/photo";

    public RestPlaceApiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        // WebClient를 빌더를 통해 생성하고, 기본 헤더를 미리 설정
        this.webClient = webClientBuilder
                .baseUrl("https://maps.googleapis.com/maps/api/place")
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                .defaultHeader("Accept", "*/*")
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * Rest Places API를 사용하여 카페 정보를 조회하고 DTO로 변환합니다.
     * 데이터베이스에서 이미지를 제외한 다른 정보가 존재하더라도, 이미지 정보가 없을 경우 API를 호출하여 보완합니다.
     * WebClient는 논블로킹 방식으로 동작합니다.
     * @param restDtoFromDb 데이터베이스에서 조회된 기존 RestDto 객체
     * @param restName 카페 이름
     * @param restBranch 카페 지점명
     * @return API로부터 보완된 RestDto 객체. API 호출 실패 시 Mono.empty()를 반환합니다.
     */
    public Mono<RestDto> getRestDetails(RestDto restDtoFromDb, String restName, String restBranch) {

        // 데이터베이스에 이미 카페 정보가 있고 이미지 주소도 있다면, 그대로 반환
        if (restDtoFromDb != null && restDtoFromDb.getRestImgAddress() != null && !restDtoFromDb.getRestImgAddress().isEmpty()) {
            logger.info("Database already contains rest details with an image. Returning existing data.");
            return Mono.just(restDtoFromDb);
        }

        // 단일 검색: 카페 이름과 지점명을 합쳐 한 번에 검색을 시도합니다.
        String query = restName;
        if (restBranch != null && !restBranch.trim().isEmpty()) {
            query += " " + restBranch;
        }
        logger.info("단일 검색: '{}'로 place_id를 찾습니다.", query);

        return findPlaceId(query)
                .flatMap(placeId -> {
                    logger.info("place_id를 찾았습니다: {}", placeId);
                    // 2단계: Place Details API를 호출하여 상세 정보를 가져옵니다.
                    return getPlaceDetails(placeId, restDtoFromDb);
                })
                .doOnError(e -> logger.error("Rest Place API 호출 중 오류 발생: {}", e.getMessage(), e))
                .onErrorResume(e -> Mono.empty()); // 오류 발생 시 빈 Mono 반환
    }

    /**
     * 지정된 쿼리로 Rest Find Place API를 호출하여 place_id를 Mono로 반환합니다.
     * @param query 검색어
     * @return 찾은 place_id, 없으면 Mono.empty()
     */
    private Mono<String> findPlaceId(String query) {

        return webClient.get()
                .uri(uriBuilder -> {
                    // 전송되는 URL을 로그로 출력
                    URI uri = uriBuilder
                            .path("/findplacefromtext/json")
                            .queryParam("input", query)
                            .queryParam("inputtype", "textquery")
                            .queryParam("fields", "place_id")
                            .queryParam("key", googlePlacesApiKey)
                            .build();
                    logger.info(">>> 전송되는 Find Place API URL: {}", uri.toString());
                    return uri; // URI를 직접 반환
                })
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(json -> logger.info(">>> Find Place API로부터 받은 원시 JSON 응답: {}", json)) // 원시 JSON 응답을 로그로 출력
                .flatMap(json -> {
                    try {
                        JsonNode root = objectMapper.readTree(json);
                        JsonNode firstCandidate = root.path("candidates").path(0);
                        if (firstCandidate.isMissingNode() || !firstCandidate.has("place_id")) {
                            logger.warn("최종적으로 place_id를 찾을 수 없습니다. API 응답을 확인하세요.");
                            return Mono.empty();
                        }
                        return Mono.just(firstCandidate.path("place_id").asText());
                    } catch (Exception e) {
                        logger.error("findPlaceId 응답 파싱 중 오류 발생: {}", e.getMessage(), e);
                        return Mono.empty();
                    }
                });
    }

    /**
     * place_id를 사용하여 Place Details API를 호출하고 DTO를 업데이트하여 Mono로 반환합니다.
     * @param placeId 상세 정보를 조회할 place_id
     * @param restDtoFromDb 데이터베이스에서 가져온 기존 RestDto 객체
     * @return API로부터 보완된 RestDto 객체. API 호출 실패 시 Mono.empty()를 반환합니다.
     */
    private Mono<RestDto> getPlaceDetails(String placeId, RestDto restDtoFromDb) {
        return webClient.get()
                .uri(uriBuilder -> {
                    // **1. 'fields' 파라미터에 'geometry'를 추가**하여 위도/경도 정보를 받아옵니다.
                    URI uri = uriBuilder
                            .path("/details/json")
                            .queryParam("place_id", placeId)
                            .queryParam("fields", "formatted_address,website,photos,rating,name,opening_hours,formatted_phone_number,geometry,editorial_summary")
                            .queryParam("key", googlePlacesApiKey)
                            .queryParam("language", "ko")
                            .build();
                    logger.info(">>> 전송되는 Details API URL: {}", uri.toString());
                    return uri; // URI를 직접 반환
                })
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(json -> logger.info(">>> Details API로부터 받은 원시 JSON 응답: {}", json)) // 원시 JSON 응답을 로그로 출력
                .flatMap(json -> {
                    try {
                        JsonNode root = objectMapper.readTree(json);
                        JsonNode result = root.path("result");

                        if (result.isMissingNode()) {
                            logger.warn("Could not find details for place_id: {}", placeId);
                            return Mono.empty();
                        }

                        // 데이터베이스에서 가져온 기존 DTO를 사용하거나, 새 DTO를 생성
                        RestDto finalRestDto = (restDtoFromDb != null) ? restDtoFromDb : new RestDto();

                        // API 응답에서 필요한 데이터를 추출하여 DTO에 설정
                        if (result.has("formatted_address")) {
                            finalRestDto.setRestAddress(result.path("formatted_address").asText(""));
                        }
                        if (result.has("website")) {
                            finalRestDto.setRestWebsite(result.path("website").asText(""));
                        }
                        if (result.has("rating")) {
                            finalRestDto.setRestRating(result.path("rating").asText("0"));
                        }
                        if (result.has("opening_hours") && result.path("opening_hours").has("weekday_text")) {
                            JsonNode openingHours = result.path("opening_hours").path("weekday_text");
                            if (openingHours.isArray() && openingHours.size() > 0) {
                                finalRestDto.setRestOpen(openingHours.path(0).asText(""));
                            }
                        }
                        if (result.has("formatted_phone_number")) {
                            finalRestDto.setRestPhonNumber(result.path("formatted_phone_number").asText(""));
                        }

                        // **2. 지도 임베드 URL 생성 로직 추가**
                        if (result.has("geometry") && result.path("geometry").has("location")) {
                            JsonNode location = result.path("geometry").path("location");
                            double lat = location.path("lat").asDouble();
                            double lng = location.path("lng").asDouble();

                            // 위도와 경도를 이용해 임베드 URL을 생성합니다.
                            // `q` 파라미터에 쿼리(`name, address`)를 인코딩하여 추가하면 지도가 더욱 정확해집니다.
                            String embedUrl = UriComponentsBuilder.fromHttpUrl("https://www.google.com/maps/embed/v1/place")
                                    .queryParam("key", googlePlacesApiKey)
                                    .queryParam("q", lat + "," + lng)
                                    .toUriString();
                            finalRestDto.setRestMapUrl(embedUrl);
                        }

                        // 사진 정보 추출 및 설정
                        JsonNode photos = result.path("photos");
                        if (photos.isArray() && photos.size() > 0) {
                            String photoReference = photos.path(0).path("photo_reference").asText();
                            String photoUrl = String.format("%s?maxwidth=400&photoreference=%s&key=%s",
                                    PLACE_PHOTO_API_URL, photoReference, googlePlacesApiKey);
                            finalRestDto.setRestImgAddress(photoUrl);
                        } else {
                            logger.info("No photos found for place_id: {}", placeId);
                        }
                        return Mono.just(finalRestDto);
                    } catch (Exception e) {
                        logger.error("getPlaceDetails 응답 파싱 중 오류 발생: {}", e.getMessage(), e);
                        return Mono.empty();
                    }
                });
    }
}