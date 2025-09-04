// src/main/java/com/kh/filter/JwtAuthenticationFilter.java
package com.kh.filter;

import com.kh.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component; // 🔥 추가
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component // 🔥 이 한 줄이 빈 등록 포인트
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/api/auth/**",
            "/api/manage/find-id",
            "/api/manage/find-pwd",
            "/api/movie/**",
            "/api/notices/**",
            "/api/editors/**",
            "/api/place/**",
            "/api/weather/**",
            "/api/cinemas/**"
    );

    private boolean isPublic(HttpServletRequest req) {
        String path = req.getRequestURI();
        return PUBLIC_PATTERNS.stream().anyMatch(p -> PATH_MATCHER.match(p, path));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) return true;
        return isPublic(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String auth = req.getHeader("Authorization");

        // 토큰 없으면 그냥 통과 (permitAll 경로 보장)
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = auth.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"error\":\"유효하지 않은 토큰입니다\"}");
            return;
        }

        String id = jwtTokenProvider.getUseridFromToken(token);
        String rolesString = jwtTokenProvider.getRolesFromToken(token);

        var authorities =
                (rolesString != null && !rolesString.isEmpty())
                        ? Arrays.stream(rolesString.split(","))
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim().toUpperCase()))
                        .collect(Collectors.toList())
                        : List.of(new SimpleGrantedAuthority("ROLE_USER"));

        var authentication = new UsernamePasswordAuthenticationToken(id, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }
}
