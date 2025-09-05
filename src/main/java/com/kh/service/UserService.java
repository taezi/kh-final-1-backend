package com.kh.service;

import com.kh.dto.MemberDTO;
import com.kh.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public MemberDTO findByUserid(String userid) {
        return userMapper.findByUserid(userid);
    }

    public void registerUser(MemberDTO user) {
        userMapper.registerUser(user);
    }

    public MemberDTO findByid(long userno) {
        return userMapper.findByid(userno);
    }

    public MemberDTO findByUsername(String beforeUsername) {
        return userMapper.findByUsername(beforeUsername);
    }


    public MemberDTO findByUserno(Long userno) {
        return userMapper.findByUserno(userno);
    }

    public MemberDTO findIdByUserInfo(String username, String nickname) {
        return userMapper.findIdByUserInfo(username, nickname);
    }

    public MemberDTO findForPwd(String userid, String username, String nickname) {
        return userMapper.findForPwd(userid, username, nickname);
    }


    public List<MemberDTO> selectAllUser() {
        return userMapper.selectAllUser();
    }

    public int deleteUserByUserno(Long userno) {
        return userMapper.deleteUserByUserno(userno);
    }


}
