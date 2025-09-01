package com.kh.service;

import com.kh.dto.MemberDTO;
import com.kh.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
