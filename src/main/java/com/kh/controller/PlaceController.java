package com.kh.controller;

import com.kh.dto.PlaceDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    /**
     * 첫 번째 페이지에서 사용될, 장소 이름과 사진을 반환하는 API입니다.
     * @return 장소 데이터(PlaceDto) 리스트 (photoUrl 필드에 값이 있을 수 있음)
     */
    @GetMapping("/places")
    public List<PlaceDto> getPlaces() {
        return placeService.getPlacesWithPhotos();
    }

    /**
     * 두 번째 페이지에서 사용될, 장소의 상세 정보를 반환하는 API입니다.
     * @param placeName 상세 정보를 조회할 장소 이름
     * @return 상세 정보가 포함된 PlaceDto
     */
    @GetMapping("/places/{placeName}")
    public PlaceDto getPlaceDetails(@PathVariable String placeName) {
        return placeService.getPlaceDetails(placeName);
    }
}
