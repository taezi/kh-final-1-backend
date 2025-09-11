package com.kh.controller;


import com.kh.dto.MemberDTO;
import com.kh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;


    @GetMapping
    public List<MemberDTO> getUserData(){
        System.out.println(userService.selectAllUser());
        return userService.selectAllUser();
    }

    @DeleteMapping
    public Map<String, Object> deleteAdminUser(@RequestBody MemberDTO memberDTO) {
        Map<String, Object> map = new HashMap<>();
        System.out.println("삭제할 회원 정보 : " + memberDTO);

        try {
            userService.deleteUserByUserno(memberDTO.getUserno());
            map.put("code",1);
            map.put("msg", "회원을 삭제하였습니다.");
        } catch (Exception e) {
            map.put("code",2);
            map.put("msg", "회원삭제를 실패하였습니다.");
        }

        return map;
    }

    // 단일 회원 조회용
    @GetMapping("/user/{userno}")
    public MemberDTO getUserByNo(@PathVariable int userno) {
        System.out.println("단일 회원 조회: " + userno);
        return userService.selectUserByUserno(userno);
    }

}
