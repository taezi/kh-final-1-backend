package com.kh.mapper;

import com.kh.dto.EventDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EventMapper {

    List<EventDto> selectPagedByDate(
            @Param("date") LocalDate date,
            @Param("district") String district,
            @Param("q") String q,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    int countByDate(
            @Param("date") LocalDate date,
            @Param("district") String district,
            @Param("q") String q
    );

    List<EventDto> selectFeatured(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("district") String district,
            @Param("limit") int limit
    );

    List<EventDto> selectBetween(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("district") String district,
            @Param("q") String q
    );

    EventDto selectById(@Param("id") Long id);

}
