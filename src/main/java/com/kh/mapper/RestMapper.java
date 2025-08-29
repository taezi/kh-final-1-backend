package com.kh.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.kh.dto.RestDto;

import java.util.List;

/**
 * RestMapper 인터페이스는 데이터베이스 작업을 위한 메서드를 정의합니다.
 * 이 인터페이스의 메서드 이름은 RestMapper.xml 파일의 쿼리 ID와 일치해야 합니다.
 * @Mapper 어노테이션은 이 인터페이스가 MyBatis 매퍼임을 나타냅니다.
 */
@Mapper
public interface RestMapper {
    /**
     * 기존 카페 정보를 업데이트하는 메서드입니다.
     * RestDto 객체를 파라미터로 받아, restNo를 기준으로 데이터를 수정합니다.
     * @param restDto 업데이트할 정보가 담긴 RestDto 객체
     * @return 업데이트된 행의 수
     */
    int updateRest(RestDto restDto);

    /**
     * 카페 이름과 지점명을 기준으로 카페 정보를 조회하는 메서드입니다.
     * @Param 어노테이션은 여러 파라미터를 XML 매퍼 파일로 전달할 때 사용됩니다.
     * @param restName 조회할 카페의 이름
     * @param restBranch 조회할 카페의 지점명
     * @return 조회된 카페 정보가 담긴 RestDto 객체
     */
    RestDto findByRestNameAndBranch(@Param("restName") String restName, @Param("restBranch") String restBranch);

    /**
     * 특정 지역구에 속한 식당 목록을 조회하는 메서드입니다. (새로 추가됨)
     * @param region 조회할 지역구 이름
     * @return 해당 지역구의 식당 목록
     */
    List<RestDto> findByRegion(String region);
}