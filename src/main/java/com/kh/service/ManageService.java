package com.kh.service;

import com.kh.dto.InquiryDTO;
import com.kh.mapper.ManageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public int insertInquiry(InquiryDTO inquiryDTO) {
        return mapper.insertInquiry(inquiryDTO);
    }

    public List<InquiryDTO> getInquiriesByUser(int userno) {
        return mapper.getInquiriesByUser(userno);
    }


    public InquiryDTO getInquiryDetail(int inquiryno) {
        return mapper.getInquiryDetail(inquiryno);
    }

    public List<InquiryDTO> getInquiriesList() {
        return mapper.getInquiriesList();
    }

    public int insertReply(InquiryDTO inquiryDTO) {
        return mapper.insertReply(inquiryDTO);
    }
}
