package com.kh.mapper;

import com.kh.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    MemberDTO findByUserid(String userid);

    void registerUser(MemberDTO user);

    MemberDTO findByid(long userno);

    MemberDTO findByUsername(String beforeUsername);

    List<MemberDTO> selectAllUser();



    MemberDTO selectUserByUserno(int userno);

}
