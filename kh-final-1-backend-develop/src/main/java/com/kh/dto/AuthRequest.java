package com.kh.dto;

import lombok.Data;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;

@ToString
@Data
public class AuthRequest {
    @NotBlank(message = "사용자 아이디는 비워둘 수 없습니다.")
    private String userid;
    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;
    private String username;
    private String nickname;
    private String email;

}
