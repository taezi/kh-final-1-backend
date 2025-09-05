package com.kh.controller;

import com.kh.dto.AuthRequest;
import com.kh.dto.JwtResponse;
import com.kh.dto.MemberDTO;
import com.kh.service.UserService;
import com.kh.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwt;


    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody AuthRequest req){
        if(userService.findByUserid(req.getUserid()) != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자 아이디가 이미 존재합니다.");
        }
        MemberDTO user = new MemberDTO();
        user.setUserid(req.getUserid());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUsername(req.getUsername());
        user.setRole("user");
        user.setEmail(req.getEmail());
        user.setNickname(req.getNickname());
        userService.registerUser(user);
        System.out.println(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("사용자가 정상적으로 등록되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody AuthRequest req, HttpServletResponse res){
        MemberDTO user = userService.findByUserid(req.getUserid());
        if(user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String access = jwt.generateAccessToken(user);
        String refresh = jwt.generateRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken", refresh);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // prod: true (HTTPS)
//        cookie.setAttribute("SameSite", "Strict"); 배포시 위에랑 같이 변경
        cookie.setPath("/");
        cookie.setMaxAge((int)(jwt.getRefreshTokenExpirationMs()/1000));
        res.addCookie(cookie);
        System.out.println(user);

        return ResponseEntity.ok(new JwtResponse(access, refresh, "Bearer", user));
    }

    /** Access 만료 시 호출: 쿠키의 refreshToken으로 Access 재발급 */
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(HttpServletRequest req , HttpServletResponse res) {
        System.out.println("==== REFRESH TOKEN 요청 들어옴 ====");
        String refresh = Arrays.stream(Optional.ofNullable(req.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst().map(Cookie::getValue).orElse(null);

        if (refresh == null || !jwt.validateToken(refresh)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userno = jwt.getUseridFromToken(refresh);
        MemberDTO user = userService.findByid(Long.parseLong(userno));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        System.out.println(user);


        //  새 Access Token & Refresh Token 발급
        String newAccess = jwt.generateAccessToken(user);
        String newRefresh = jwt.generateRefreshToken(user);

        // 새 Refresh Token 쿠키로 내려주기
        Cookie cookie = new Cookie("refreshToken", newRefresh);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); //  운영 배포 시 true로 변경
        cookie.setPath("/");
        cookie.setMaxAge((int)(jwt.getRefreshTokenExpirationMs() / 1000));
        res.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(newAccess, newRefresh, "Bearer", user));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse res){
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // prod: true
        cookie.setPath("/");
        cookie.setMaxAge(0);
        res.addCookie(cookie);
        return ResponseEntity.ok("성공적으로 로그아웃 되었습니다.");
    }

    /** 보호된 API: 필터가 넣어준 attribute 사용 */
    @GetMapping("/user-data")
    public ResponseEntity<MemberDTO> getUserData(@AuthenticationPrincipal String userId){
        String userno = userId;
        if (userno == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        MemberDTO user = userService.findByid(Long.parseLong(userno));
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(user);
    }
}
