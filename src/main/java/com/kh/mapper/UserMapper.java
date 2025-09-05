package com.kh.mapper;

import com.kh.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    MemberDTO findByUserid(String userid);

    void registerUser(MemberDTO user);

    MemberDTO findByid(long userno);

    MemberDTO findByUsername(String beforeUsername);

    MemberDTO findByUserno(Long userno);
}
