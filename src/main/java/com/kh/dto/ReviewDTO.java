package com.kh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long userno;
    private Long reviewno;
    private String commenta;
    private String createdat;
    private String contenttype;
    private Long contentno;
    private String username;
}
