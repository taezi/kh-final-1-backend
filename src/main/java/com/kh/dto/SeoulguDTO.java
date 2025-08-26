package com.kh.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("seoulgu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeoulguDTO {
    private String seoulname;
    private int nx;
    private int ny;
    private String msradmcode;
}
