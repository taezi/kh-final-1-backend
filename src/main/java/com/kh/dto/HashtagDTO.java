package com.kh.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("hashtag")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HashtagDTO {

    private Long hashtagid;
    private String tagname;

}
