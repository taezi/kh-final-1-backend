package com.kh.controller;


import com.kh.dto.InquiryDTO;
import com.kh.dto.MemberDTO;
import com.kh.service.ManageService;
import com.kh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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


    @PostMapping("/inquiry")
    public Map<String, Object> createInquiry(@RequestBody InquiryDTO inquiryDTO){
        Map<String, Object> map = new HashMap<>();
        System.out.println("문의글 내용 : " + inquiryDTO);
        try {
            int result = manageService.insertInquiry(inquiryDTO);
            if (result > 0) {
                map.put("code", 1);
                map.put("msg", "문의가 성공적으로 등록되었습니다.");
                return map;
            } else {
                map.put("code", 2);
                map.put("msg", "문의 등록에 실패했습니다. (영향받은 행 없음)");
                return map;
            }
        } catch (Exception e) {
            System.err.println("문의 등록 중 오류 발생: " + e.getMessage());
            map.put("code", 2);
            map.put("msg", "문의 등록에 실패했습니다. 다시 시도해 주세요.");
            return map;
        }
    }


    @GetMapping("/inquiry/list")
    public List<InquiryDTO> getAllInquiries() {
        System.out.println("모든 1:1 문의 리스트 가져오기");

        return manageService.getInquiriesList();
    }


    @GetMapping("/inquiry/list/{userno}")
    public List<InquiryDTO> getInquiries(@PathVariable int userno) {

        return manageService.getInquiriesByUser(userno);
    }

    @GetMapping("/inquiry/detail/{inquiryno}")
    public InquiryDTO getInquiryDetail(@PathVariable int inquiryno) {
        System.out.println("조회할 문의 번호: " + inquiryno);
        return manageService.getInquiryDetail(inquiryno);
    }


    @PostMapping("/inquiry/reply")
    public ResponseEntity<?> createInquiryReply(@RequestBody InquiryDTO inquiryDTO){
        System.out.println("1:1문의(답변)" + inquiryDTO);
        manageService.insertReply(inquiryDTO);
        return ResponseEntity.ok("답변완료");
    }

    @GetMapping("/find-id")
    public ResponseEntity<?> findId(@RequestParam String username,
                                    @RequestParam String nickname) {
        System.out.println("aaaaa"+ username +  " "+nickname);
        MemberDTO member = userService.findIdByUserInfo(username, nickname);
        String foundId = (member != null) ? member.getUserid() : null;
        System.out.println(member);
        return ResponseEntity.ok(Map.of("foundId", foundId));
    }

    @PostMapping("/find-pwd")
    public ResponseEntity<?> findPwd(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String userid   = body.get("userid");
        String nickname = body.get("nickname");

        // 입력 로깅
        System.out.println("[find-pwd] username=" + username + ", userid=" + userid + ", nickname=" + nickname);

        MemberDTO member = userService.findForPwd(userid, username, nickname);
        if (member == null) {
            return ResponseEntity.ok(Map.of("tempPassword", null));
        }

        // 임시 비밀번호 생성 (영문/숫자 10자)
        String temp = generateTempPassword(10);

        // 암호화 후 저장
        String encoded = passwordEncoder.encode(temp);
        manageService.updatePassword(userid, encoded);

        // 보안상 실제 서비스에서는 화면에 노출하지 말고 이메일/SMS 발송 권장
        return ResponseEntity.ok(Map.of("tempPassword", temp));
    }

    // 간단 임시 비밀번호 생성 유틸(컨트롤러 내 private 메서드)
    private String generateTempPassword(int len) {
        String base = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder(len);
        java.security.SecureRandom r = new java.security.SecureRandom();
        for (int i = 0; i < len; i++) sb.append(base.charAt(r.nextInt(base.length())));
        return sb.toString();
    }



}
