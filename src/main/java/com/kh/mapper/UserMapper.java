package com.kh.mapper;

import com.kh.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface UserMapper {
    MemberDTO findByUserid(String userid);

    void registerUser(MemberDTO user);

    MemberDTO findByid(long userno);

    MemberDTO findByUsername(String beforeUsername);

    MemberDTO findIdByUserInfo(@Param("username") String username,
                               @Param("nickname") String nickname);

    MemberDTO findForPwd(@Param("userid") String userid,
                         @Param("username") String username,
                         @Param("nickname") String nickname);


}
