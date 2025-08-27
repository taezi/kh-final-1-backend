package com.kh.mapper;

import com.kh.dto.Cafe;
import com.kh.dto.CafeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CafeMapper {
    List<CafeDto> findAllCafes();
    void updateCafeDetails(CafeDto cafe);
    Cafe findCafeByName(@Param("cafeName") String cafeName);
}//