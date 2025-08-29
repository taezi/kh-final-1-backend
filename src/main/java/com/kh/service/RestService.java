package com.kh.service;


import com.kh.dto.RestDto;
import com.kh.mapper.RestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;


/**
 * RestService 클래스는 비즈니스 로직을 처리하는 서비스 계층입니다.
 * RestMapper와 RestPlaceApiService를 주입받아 데이터베이스 및 외부 API 작업을 수행합니다.
 */
@Service
public class RestService {

    // 로거 설정
    private static final Logger logger = LoggerFactory.getLogger(RestService.class);

    @Autowired
    private RestMapper restMapper;

    @Autowired
    private RestPlaceApiService restPlaceApiService;

    /**
     * 기존 카페 정보를 업데이트합니다.
     * @param restDto 업데이트할 정보가 담긴 RestDto 객체
     * @return 업데이트 성공 여부 (true/false)
     */
    public boolean updateRest(RestDto restDto) {
        // 업데이트된 행의 수가 1이면 성공으로 간주
        return restMapper.updateRest(restDto) == 1;
    }

    /**
     * 카페 이름과 지점명을 기준으로 카페 정보를 조회하고, 이미지가 없을 경우 Google API를 통해 정보를 보완합니다.
     * @param restName 조회할 카페의 이름
     * @param restBranch 조회할 카페의 지점명
     * @return 최종적으로 조회되거나 보완된 카페 정보
     */
    public RestDto getRestByNameAndBranch(String restName, String restBranch) throws UnsupportedEncodingException {
        // 1. 데이터베이스에서 먼저 카페 정보를 조회합니다.
        RestDto restFromDb = restMapper.findByRestNameAndBranch(restName, restBranch);

        // 2. DB에 정보가 없거나 이미지가 없는 경우, Rest API를 호출하여 정보를 보완합니다.
        RestDto finalRestDto = restPlaceApiService.getRestDetails(restFromDb, restName, restBranch).block();

        // 3. API 호출을 통해 정보가 보완되었다면, DB에 저장(업데이트)합니다.
        if (finalRestDto != null) {
            if (restFromDb != null) {
                // 기존 데이터가 있으면 업데이트
                finalRestDto.setRestNo(restFromDb.getRestNo()); // PK 설정
                restMapper.updateRest(finalRestDto);
            } else {
                // 기존 데이터가 없으면 새로 추가
                // restMapper.insertRest(finalRestDto);
                // 삽입 로직은 필요에 따라 추가
            }
        }
        return finalRestDto;
    }
}