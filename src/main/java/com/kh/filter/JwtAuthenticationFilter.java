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

    // SecurityConfig의 permitAll과 정확히 일치하도록 수정
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/",
            "/api/place/",
            "/api/weather/",
            "/api/movies/",
            "/api/cinemas/"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        System.out.println("shouldNotFilter 체크 - 경로: " + path + ", 메소드: " + method);

        // OPTIONS 요청 (CORS preflight) 통과
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // GET 요청의 공지사항 조회는 인증 불필요
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/api/notices")) {
            System.out.println("공지사항 GET 요청 - 인증 불필요");
            return true;
        }
        //  GET 요청의 에디터 게시판 조회는 인증 불필요
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/api/editors")) {
            return true;
        }

        // 기타 완전 공개 경로 확인
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(publicPath -> {
            if (publicPath.endsWith("/")) {
                return path.startsWith(publicPath);
            } else {
                return path.equals(publicPath) || path.startsWith(publicPath + "/");
            }
        });

        if (isPublic) {
            System.out.println("공개 경로 - 인증 불필요");
        }

        return isPublic;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        System.out.println("JWT 필터 처리 - 경로: " + req.getServletPath() + ", 메소드: " + req.getMethod());

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
            System.out.println("토큰이 없음 - 경로: " + req.getServletPath());
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