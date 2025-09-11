package com.kh.service;


import com.kh.dto.CafeDto;
import com.kh.mapper.CafeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * CafeService 클래스는 비즈니스 로직을 처리하는 서비스 계층입니다.
 * CafeMapper와 CafePlaceApiService를 주입받아 데이터베이스 및 외부 API 작업을 수행합니다.
 */
@Service
public class CafeService {

    // 로거 설정
    private static final Logger logger = LoggerFactory.getLogger(CafeService.class);

    @Autowired
    private CafeMapper cafeMapper;

    @Autowired
    private GooglePlaceApiService googlePlaceApiService;

    /**
     * 기존 카페 정보를 업데이트합니다.
     * @param cafeDto 업데이트할 정보가 담긴 CafeDto 객체
     * @return 업데이트 성공 여부 (true/false)
     */
    public boolean updateCafe(CafeDto cafeDto) {
        // 업데이트된 행의 수가 1이면 성공으로 간주
        return cafeMapper.updateCafe(cafeDto) == 1;
    }
    public CafeDto getCafeByNo(int cafeNo) {
        // 1. 데이터베이스에서 cafeNo를 기준으로 카페 정보를 조회합니다.
        CafeDto cafeFromDb = cafeMapper.findByCafeNo(cafeNo);

        // 2. DB에 정보가 없거나, 이미지가 없는 경우에만 외부 API를 호출하여 정보를 보완합니다.
        if (cafeFromDb == null || cafeFromDb.getCafeImgAddress() == null || cafeFromDb.getCafeImgAddress().isEmpty()) {
            if (cafeFromDb != null) {
                // Google API는 cafeName과 cafeBranch를 사용하므로, DB에서 가져온 값을 전달
                CafeDto finalCafeDto = googlePlaceApiService
                        .getCafeDetails(cafeFromDb, cafeFromDb.getCafeName(), cafeFromDb.getCafeBranch())
                        .block();

                if (finalCafeDto != null) {
                    // API 호출로 정보가 보완되었으면 DB에 업데이트
                    finalCafeDto.setCafeNo(cafeFromDb.getCafeNo()); // PK 설정
                    cafeMapper.updateCafe(finalCafeDto);
                    return finalCafeDto;
                }
            }
            return cafeFromDb; // API 호출 실패 또는 DB에 데이터 자체가 없는 경우
        }

        // 3. DB에 이미 정보와 이미지가 모두 있는 경우 그대로 반환
        return cafeFromDb;
    }

    /**
     * 카페 이름과 지점명을 기준으로 카페 정보를 조회하고, 이미지가 없을 경우 Google API를 통해 정보를 보완합니다.
     * @param cafeName 조회할 카페의 이름
     * @param cafeBranch 조회할 카페의 지점명
     * @return 최종적으로 조회되거나 보완된 카페 정보
     */

    public CafeDto getCafeByNameAndBranch(String cafeName, String cafeBranch) throws UnsupportedEncodingException {
        // 1. 데이터베이스에서 먼저 카페 정보를 조회합니다.
        CafeDto cafeFromDb = cafeMapper.findByCafeNameAndBranch(cafeName, cafeBranch);

        // 2. DB에 정보가 없거나 이미지가 없는 경우, Cafe API를 호출하여 정보를 보완합니다.
        CafeDto finalCafeDto = googlePlaceApiService.getCafeDetails(cafeFromDb, cafeName, cafeBranch).block();

        // 3. API 호출을 통해 정보가 보완되었다면, DB에 저장(업데이트)합니다.
        if (finalCafeDto != null) {
            if (cafeFromDb != null) {
                // 기존 데이터가 있으면 업데이트
                finalCafeDto.setCafeNo(cafeFromDb.getCafeNo()); // PK 설정
                cafeMapper.updateCafe(finalCafeDto);
            }
        }
        return finalCafeDto;
    }

    /* 특정 지역구(gu)와 검색어(q)를 사용하여 식당 목록을 페이징하여 조회하는 메서드입니다.
     * @param gu 조회할 지역구 이름 (선택적)
     * @param q 조회할 검색어 (선택적)
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return 조회된 식당 목록과 페이징 정보 (hasMore)
     */
    public Map<String, Object> searchCafes(String gu, String q, int page, int size) {
        // 1. 전체 데이터 수 조회
        // 이 쿼리를 통해 특정 'gu'와 'q'에 해당하는 전체 식당 수를 가져옵니다.
        int totalCount = cafeMapper.countByGuAndQuery(gu, q);

        // 2. 페이징 처리를 위한 offset 계산 (페이지 번호는 1부터 시작)
        int offset = (page - 1) * size;

        // 3. 현재 페이지에 해당하는 식당 목록을 가져옵니다.
        List<CafeDto> cafes = cafeMapper.findByGuAndQuery(gu, q, offset, size);

        // 4. 다음 페이지가 있는지 확인
        // (현재 페이지 번호 * 페이지당 항목 수)가 전체 항목 수보다 작으면 다음 페이지가 존재합니다.
        boolean hasMore = (long) page * size < totalCount;

        // 5. 결과를 맵에 담아 반환합니다.
        Map<String, Object> result = new HashMap<>();
        result.put("items", cafes);
        result.put("hasMore", hasMore);

        return result;
    }
}
