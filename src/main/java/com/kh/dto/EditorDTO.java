package com.kh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditorDTO {
    private String editortitle;   // 제목
    private String editorcontent; // 내용
//    private String editor_file;// 에디터 이미지
}