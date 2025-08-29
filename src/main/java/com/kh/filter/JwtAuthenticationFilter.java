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
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // SecurityConfig의 permitAll과 동일하게 맞춤
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/",
            "/api/place/",
            "/api/weather/",
            "/api/editor/list",
            "/api/editor/detail/",
            "/api/notice",
            "/api/movies/",
            "/api/cinemas/"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        System.out.println("shouldNotFilter 체크 경로: " + path);

        // OPTIONS 요청 (CORS preflight) 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 공개 경로 확인 - 정확한 패턴 매칭
        return PUBLIC_PATHS.stream().anyMatch(publicPath -> {
            if (publicPath.endsWith("/")) {
                return path.startsWith(publicPath);
            } else {
                return path.equals(publicPath) || path.startsWith(publicPath + "/");
            }
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        System.out.println("JWT 필터 처리 경로: " + req.getServletPath());

        String auth = req.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            if (jwtTokenProvider.validateToken(token)) {
                String id = jwtTokenProvider.getUseridFromToken(token);
                String roles = jwtTokenProvider.getRolesFromToken(token);

                // Spring Security 권한 객체 생성
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + roles.toUpperCase()));

                // 인증 객체 생성 및 SecurityContext 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(id, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("인증 성공 - 사용자 ID: " + id + ", 권한: " + authorities);
            } else {
                System.out.println("유효하지 않은 토큰");
                sendUnauthorizedResponse(res, "유효하지 않은 토큰입니다");
                return;
            }
        } else {
            System.out.println("토큰이 없음");
            sendUnauthorizedResponse(res, "Authorization 헤더가 없거나 Bearer 토큰이 아닙니다");
            return;
        }

        chain.doFilter(req, res);
    }

    private void sendUnauthorizedResponse(HttpServletResponse res, String message) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}