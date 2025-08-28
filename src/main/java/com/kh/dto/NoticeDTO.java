package com.kh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDTO {
    private long userno;
    private long noticeno;
    private String noticetitle;
    private String noticepost;
    private String noticedate;
    private long noticeview;
    private String noticeupdatedate;
}
