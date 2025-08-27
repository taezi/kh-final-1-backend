package com.kh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.dto.Cafe;
import com.kh.dto.RestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class GooglePlaceApiService {

    @Value("${google.api-key}")
    private String googleApiKey;

    private static final String FIND_PLACE_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json";
    private static final String PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json";

    private final WebClient webClient = WebClient.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 장소 이름을 받아 구글 API를 통해 장소 ID를 찾습니다.
     * @param query 검색할 쿼리 문자열
     * @return 찾은 place_id, 없으면 null 반환
     */
    private String findPlaceId(String query) {
        try {
            String findResult = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(FIND_PLACE_URL)
                            .queryParam("input", query)
                            .queryParam("inputtype", "textquery")
                            .queryParam("fields", "place_id")
                            .queryParam("key", googleApiKey)
                            .build())
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(body -> new RuntimeException("API Response Error: " + response.statusCode() + " " + body))
                    )
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(findResult);
            JsonNode candidatesNode = rootNode.path("candidates");

            if (candidatesNode.isArray() && candidatesNode.size() > 0) {
                return candidatesNode.get(0).path("place_id").asText();
            } else {
                System.err.println("Error: 'candidates' node is missing or empty in the API response for " + query);
            }
        } catch (WebClientResponseException e) {
            System.err.println("WebClient Error - Status: " + e.getRawStatusCode() + ", Body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 장소 이름과 지점명을 받아 상세 정보를 조회하고 Cafe DTO로 변환합니다.
     * @param placeName 검색할 장소 이름
     * @param placeBranch 검색할 장소 지점명
     * @return 상세 정보가 채워진 Cafe DTO, 실패 시 null 반환
     */
    public Cafe getCafeDetails(String cafeName, String cafeBranch) {
        // 이름과 지점명을 합쳐서 검색 쿼리를 생성합니다.
        String placeQuery = cafeName + " " + cafeBranch;
        String placeId = findPlaceId(placeQuery);
        if (placeId == null) return null;

        try {
            String detailsResult = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(PLACE_DETAILS_URL)
                            .queryParam("place_id", placeId)
                            .queryParam("fields", "formatted_address,photos,rating,types,editorial_summary,formatted_phone_number,website,opening_hours,url")
                            .queryParam("key", googleApiKey)
                            .build())
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(body -> new RuntimeException("API Response Error: " + response.statusCode() + " " + body))
                    )
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(detailsResult);
            JsonNode resultNode = rootNode.path("result");

            if (!resultNode.isMissingNode()) {
                Cafe cafe = new Cafe();
                cafe.setCafeName(cafeName);
                cafe.setCafeAddress(resultNode.path("formatted_address").asText(null));
                cafe.setCafeRating(resultNode.path("rating").asText(null));
                cafe.setCagePhonNumber(resultNode.path("formatted_phone_number").asText(null));
                cafe.setCafeWebsite(resultNode.path("website").asText(null));
                cafe.setCafeMapUrl(resultNode.path("url").asText(null));

                JsonNode photosNode = resultNode.path("photos");
                if (photosNode.isArray() && photosNode.size() > 0) {
                    String photoRef = photosNode.get(0).path("photo_reference").asText();
                    String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoRef + "&key=" + googleApiKey;
                    cafe.setCafeImgAddress(photoUrl);
                }

                JsonNode typesNode = resultNode.path("types");
                if (typesNode.isArray() && typesNode.size() > 0) {
                    cafe.setCafeType(typesNode.get(0).asText());
                }
                JsonNode summaryNode = resultNode.path("editorial_summary");
                if (!summaryNode.isMissingNode()) {
                    cafe.setCafeSummary(summaryNode.path("overview").asText());
                }
                JsonNode openingHoursNode = resultNode.path("opening_hours");
                if (!openingHoursNode.isMissingNode()) {
                    cafe.setCafeOpen(openingHoursNode.path("weekday_text").toString());
                }
                return cafe;
            }
        } catch (WebClientResponseException e) {
            System.err.println("WebClient Error - Status: " + e.getRawStatusCode() + ", Body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 장소 ID를 받아 상세 정보를 조회하고 RestDto DTO로 변환합니다.
     *
     * @param restName 검색할 장소 이름
     * @return 상세 정보가 채워진 RestDto DTO, 실패 시 null 반환
     */
    public RestDto getRestDetails(String restName, String restBranch) {
        // 이름과 지점명을 합쳐서 검색 쿼리를 생성합니다.
        String placeQuery = restName + " " + restBranch;
        String placeId = findPlaceId(placeQuery);
        if (placeId == null) return null;

        try {
            String detailsResult = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(PLACE_DETAILS_URL)
                            .queryParam("place_id", placeId)
                            .queryParam("fields", "formatted_address,photos,rating,types,editorial_summary,formatted_phone_number,website,opening_hours,url")
                            .queryParam("key", googleApiKey)
                            .build())
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(body -> new RuntimeException("API Response Error: " + response.statusCode() + " " + body))
                    )
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(detailsResult);
            JsonNode resultNode = rootNode.path("result");

            if (!resultNode.isMissingNode()) {
                RestDto rest = new RestDto();
                rest.setRestName(restName);
                rest.setRestAddress(resultNode.path("formatted_address").asText(null));
                rest.setRestRating(resultNode.path("rating").asText(null));
                rest.setRestPhonNumber(resultNode.path("formatted_phone_number").asText(null));
                rest.setRestWebsite(resultNode.path("website").asText(null));
                rest.setRestMapUrl(resultNode.path("url").asText(null));

                JsonNode photosNode = resultNode.path("photos");
                if (photosNode.isArray() && photosNode.size() > 0) {
                    String photoRef = photosNode.get(0).path("photo_reference").asText();
                    String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoRef + "&key=" + googleApiKey;
                    rest.setRestImgAddress(photoUrl);
                }

                JsonNode typesNode = resultNode.path("types");
                if (typesNode.isArray() && typesNode.size() > 0) {
                    rest.setRestType(typesNode.get(0).asText());
                }
                JsonNode summaryNode = resultNode.path("editorial_summary");
                if (!summaryNode.isMissingNode()) {
                    rest.setRestSummary(summaryNode.path("overview").asText());
                }
                JsonNode openingHoursNode = resultNode.path("opening_hours");
                if (!openingHoursNode.isMissingNode()) {
                    rest.setRestOpen(openingHoursNode.path("weekday_text").toString());
                }
                return rest;
            }
        } catch (WebClientResponseException e) {
            System.err.println("WebClient Error - Status: " + e.getRawStatusCode() + ", Body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}