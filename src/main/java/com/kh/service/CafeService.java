package com.kh.service;

import com.kh.dto.CafeDto;
import com.kh.mapper.CafeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;


/**
 * CafeService 클래스는 비즈니스 로직을 처리하는 서비스 계층입니다.
 * CafeMapper와 GooglePlaceApiService를 주입받아 데이터베이스 및 외부 API 작업을 수행합니다.
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

    /**
     * 카페 이름과 지점명을 기준으로 카페 정보를 조회하고, 이미지가 없을 경우 Google API를 통해 정보를 보완합니다.
     * @param cafeName 조회할 카페의 이름
     * @param cafeBranch 조회할 카페의 지점명
     * @return 최종적으로 조회되거나 보완된 카페 정보
     */
    public CafeDto getCafeByNameAndBranch(String cafeName, String cafeBranch) throws UnsupportedEncodingException {
        // 1. 데이터베이스에서 먼저 카페 정보를 조회합니다.
        CafeDto cafeFromDb = cafeMapper.findByCafeNameAndBranch(cafeName, cafeBranch);

        // 2. DB에 정보가 없거나 이미지가 없는 경우, Google API를 호출하여 정보를 보완합니다.
        CafeDto finalCafeDto = googlePlaceApiService.getCafeDetails(cafeFromDb, cafeName, cafeBranch).block();

        // 3. API 호출을 통해 정보가 보완되었다면, DB에 저장(업데이트)합니다.
        if (finalCafeDto != null) {
            if (cafeFromDb != null) {
                // 기존 데이터가 있으면 업데이트
                finalCafeDto.setCafeNo(cafeFromDb.getCafeNo()); // PK 설정
                cafeMapper.updateCafe(finalCafeDto);
            } else {
                // 기존 데이터가 없으면 새로 추가
                // cafeMapper.insertCafe(finalCafeDto);
                // 삽입 로직은 필요에 따라 추가
            }
        }
        return finalCafeDto;
    }
}