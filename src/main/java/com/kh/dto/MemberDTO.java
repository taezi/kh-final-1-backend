package com.kh.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long userno;
    private String userid;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String joindate;
    private String role;
    //12
}
