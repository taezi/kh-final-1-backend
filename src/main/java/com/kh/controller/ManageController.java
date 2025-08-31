package com.kh.controller;


import com.kh.dto.MemberDTO;
import com.kh.service.ManageService;
import com.kh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/manage")
@RestController
@RequiredArgsConstructor
public class ManageController {

    @Autowired
    private ManageService manageService;
    @Autowired
    private UserService userService;
    private final PasswordEncoder passwordEncoder;

    @DeleteMapping
    public Map<String, Object> deleteUser(@RequestBody MemberDTO memberDTO){
        Map<String, Object> map = new HashMap<>();
        System.out.println("삭제할 회원 정보 : " + memberDTO);
        MemberDTO user = userService.findByUserid(memberDTO.getUserid());
        System.out.println("비교 결과: " + passwordEncoder.matches(memberDTO.getPassword(), user.getPassword()));

        if(user == null || !passwordEncoder.matches(memberDTO.getPassword(), user.getPassword())) {
            map.put("code", 2);
            map.put("msg", "비밀번호가 옳지 않습니다.");
        } else {
            manageService.deleteUser(memberDTO.getUserid());
            map.put("code", 1);
            map.put("msg", "회원탈퇴에 성공하였습니다..");
        }
        return map;
    }

    @PutMapping("/userid")
    public Map<String, Object> updateUserid(@RequestBody Map<String, String> body){
        String beforeUserid = body.get("before");
        String afterUserid = body.get("after");
        Map<String, Object> map = new HashMap<>();
        MemberDTO user = userService.findByUserid(beforeUserid);

        if (user == null) {
            map.put("code", 2);
            map.put("msg", "해당 아이디의 회원이 존재하지 않습니다.");
            return map;
        }
        MemberDTO existingUser = userService.findByUserid(afterUserid);
        if (existingUser != null) {
            map.put("code", 3);
            map.put("msg", "아이디가 이미 사용 중입니다.");
            return map;
        }

        manageService.updateUserid(beforeUserid, afterUserid);
        map.put("code", 1);
        map.put("msg", "아이디 변경에 성공하였습니다.");
        return map;
    }

    @PutMapping("/username")
    public Map<String, Object> updateUsername(@RequestBody Map<String, String> body){
        String userid = body.get("userid");
        String afterUsername = body.get("after");
        Map<String, Object> map = new HashMap<>();

        MemberDTO user = userService.findByUserid(userid);

        if (user == null) {
            map.put("code", 2);
            map.put("msg", "해당 아이디의 회원이 존재하지 않습니다.");
            return map;
        }

        manageService.updateUsername(userid, afterUsername);
        map.put("code", 1);
        map.put("msg", "이름 변경에 성공하였습니다.");
        return map;
    }

    @PutMapping("/nickname")
    public Map<String, Object> updateNickname(@RequestBody Map<String, String> body){
        String userid = body.get("userid");
        String afterNickname= body.get("after");
        Map<String, Object> map = new HashMap<>();

        MemberDTO user = userService.findByUserid(userid);

        if (user == null) {
            map.put("code", 2);
            map.put("msg", "해당 아이디의 회원이 존재하지 않습니다.");
            return map;
        }

        manageService.updateNickname(userid, afterNickname);
        map.put("code", 1);
        map.put("msg", "닉네임 변경에 성공하였습니다.");
        return map;
    }

    @PutMapping("/email")
    public Map<String, Object> updateEmail(@RequestBody Map<String, String> body){
        String userid = body.get("userid");
        String afterEmail= body.get("after");
        Map<String, Object> map = new HashMap<>();

        MemberDTO user = userService.findByUserid(userid);

        if (user == null) {
            map.put("code", 2);
            map.put("msg", "해당 아이디의 회원이 존재하지 않습니다.");
            return map;
        }

        manageService.updateEmail(userid, afterEmail);
        map.put("code", 1);
        map.put("msg", "이메일 변경에 성공하였습니다.");
        return map;
    }

    @PutMapping("/password")
    public Map<String, Object> updatePassword(@RequestBody Map<String, String> body){
        String userid = body.get("userid");
        String beforePassword= body.get("before");
        String afterPassword= body.get("after");

        Map<String, Object> map = new HashMap<>();

        MemberDTO user = userService.findByUserid(userid);

        if (user == null) {
            map.put("code", 2);
            map.put("msg", "해당 아이디의 회원이 존재하지 않습니다.");
            return map;
        }

        if (!passwordEncoder.matches(beforePassword, user.getPassword())) {
            map.put("code", 3);
            map.put("msg", "현재 비밀번호가 일치하지 않습니다.");
            return map;
        }

        String encodedAfterPassword = passwordEncoder.encode(afterPassword);

        manageService.updatePassword(userid, encodedAfterPassword);
        map.put("code", 1);
        map.put("msg", "비밀번호 변경에 성공하였습니다. 로그인을 다시 해주세요.");
        return map;
    }


}
