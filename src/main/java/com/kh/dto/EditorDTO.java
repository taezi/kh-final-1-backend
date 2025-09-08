package com.kh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditorDTO {
    private long userno; // 유저 고유번호
    private long editorno;
    private String editortitle;   // 제목
    private String editorcontent; // 내용
    private String editordate;
    private String editorupdatedate;
    private long editorview;
    private String thumbnailUrl;
    private String contentImgUrl;

    private List<String> hashtags;



}