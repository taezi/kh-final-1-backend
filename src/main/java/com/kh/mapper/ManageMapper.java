package com.kh.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ManageMapper {
    int deleteUser(String userid);

    int updateUserid(String beforeUserid, String afterUserid);

    int updateUsername(String userid, String afterUsername);

    int updateNickname(String userid, String afterNickname);

    int updateEmail(String userid, String afterEmail);

    int updatePassword(String userid, String encodedAfterPassword);
}
