package com.kh.mapper;

import com.kh.dto.Place;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PlaceMapper {
    /**
     * 모든 장소 목록을 DB에서 조회합니다.
     * @return Place 객체 리스트
     */
    List<Place> findAllPlaces();

    /**
     * 특정 장소의 상세 정보를 DB에 업데이트합니다.
     * @param place 업데이트할 모든 필드를 담고 있는 Place 객체
     */
    void updateCafeDetails(Place place);

    /**
     * 특정 장소의 이름을 기반으로 Place 객체를 조회합니다.
     * @param placeName 조회할 장소 이름
     * @return Place 객체
     */
    Place findPlaceByName(@Param("placeName") String placeName);
}