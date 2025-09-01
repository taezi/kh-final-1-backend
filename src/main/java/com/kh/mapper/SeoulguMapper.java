package com.kh.mapper;

import com.kh.dto.SeoulguDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeoulguMapper {
    SeoulguDTO findseoul(String gu);
}
