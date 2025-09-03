package com.kh.mapper;

import com.kh.dto.InquiryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ManageMapper {
    int deleteUser(String userid);

    int updateUserid(String beforeUserid, String afterUserid);

    int updateUsername(String userid, String afterUsername);

    int updateNickname(String userid, String afterNickname);

    int updateEmail(String userid, String afterEmail);

    int updatePassword(String userid, String encodedAfterPassword);

    int insertInquiry(InquiryDTO inquiryDTO);

    List<InquiryDTO> getInquiriesByUser(int userno);


    InquiryDTO getInquiryDetail(int inquiryno);

    List<InquiryDTO> getInquiriesList();


    int insertReply(InquiryDTO inquiryDTO);
}
