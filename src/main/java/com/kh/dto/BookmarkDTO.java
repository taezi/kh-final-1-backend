package com.kh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("bookmark")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {
    private String userno;
    private String contentno;
    private String contenttype;
}
