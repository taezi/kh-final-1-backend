package com.kh.filter;

import com.kh.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        System.out.println("### JWT Filter: doFilterInternal 시작 - 경로: " + req.getServletPath() + ", 메소드: " + req.getMethod());

        String auth = req.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            if (jwtTokenProvider.validateToken(token)) {
                String id = jwtTokenProvider.getUseridFromToken(token);
                String rolesString = jwtTokenProvider.getRolesFromToken(token);

                List<SimpleGrantedAuthority> authorities;
                if (rolesString != null && !rolesString.isEmpty()) {
                    authorities = Arrays.stream(rolesString.split(","))
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase()))
                            .collect(Collectors.toList());
                } else {
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(id, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("### JWT Filter: 인증 성공 - 사용자 ID: " + id + ", 권한: " + authorities);
            } else {
                System.out.println("### JWT Filter: 유효하지 않은 토큰 감지.");
                sendUnauthorizedResponse(res, "유효하지 않은 토큰입니다");
                return;
            }
        }else {
            System.out.println("### JWT Filter: Authorization 헤더 없음 또는 Bearer 토큰 아님. 다음 필터로 진행.");
        }

        chain.doFilter(req, res);
        System.out.println("### JWT Filter: doFilterInternal 종료 - 다음 필터로 전달됨.");
    }

    private void sendUnauthorizedResponse(HttpServletResponse res, String message) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}