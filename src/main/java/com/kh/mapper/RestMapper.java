package com.kh.mapper;

import com.kh.dto.RestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RestMapper {
    List<RestDto> findAllRests();
    void updateRestDetails(RestDto rest);
    RestDto findRestByName(@Param("restName") String restName);
}