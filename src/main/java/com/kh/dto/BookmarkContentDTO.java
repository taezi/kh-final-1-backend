package com.kh.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("bookmarkcontent")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkContentDTO {
    private String bookmarkno;
    private String userno;
    private String contentno;
    private String contenttype;
    private String title;
    private String type;
    private String addedat;


}
