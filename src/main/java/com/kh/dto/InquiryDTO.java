package com.kh.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("inquiry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDTO {
    private Long userno;
    private Long inquiryno;
    private String inquiryTitle;
    private String inquiryContent;
    private String createdAt;
    private String phoneNumber;
    private String replyContent;
    private String repliedAt;
    private String status;
}
