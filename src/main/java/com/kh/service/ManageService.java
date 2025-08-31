package com.kh.service;

import com.kh.mapper.ManageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManageService {

    @Autowired
    private ManageMapper mapper;

    public int deleteUser(String userid) {
        return mapper.deleteUser(userid);
    }

    public int updateUserid(String beforeUserid, String afterUserid) {
        return mapper.updateUserid(beforeUserid, afterUserid);
    }

    public int updateUsername(String userid, String afterUsername) {
        return mapper.updateUsername(userid, afterUsername);
    }

    public int updateNickname(String userid, String afterNickname) {
        return mapper.updateNickname(userid, afterNickname);
    }

    public int updateEmail(String userid, String afterEmail) {
        return mapper.updateEmail(userid, afterEmail);
    }

    public int updatePassword(String userid, String encodedAfterPassword) {
        return mapper.updatePassword(userid, encodedAfterPassword);
    }
}
