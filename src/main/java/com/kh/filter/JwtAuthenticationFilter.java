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

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login", "/api/auth/signup", "/api/auth/refresh", "/api/place",
            "/api/weather", "/api/editor","/api/cafes"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        System.out.println("shouldNotFilter : "+path);
        // OPTIONS(CORS preflight) 패스
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        System.out.println(req.getContextPath());
        String auth = req.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            if (jwtTokenProvider.validateToken(token)) {
                String id = jwtTokenProvider.getUseridFromToken(token);
                String roles = jwtTokenProvider.getRolesFromToken(token);

                // Spring Security가 인식할 수 있는 권한 객체로 변환
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority(roles));

                // 인증 객체 생성 후 SecurityContextHolder에 저장
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(id, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                /////


                // 컨트롤러에서 꺼내 쓰도록 request attribute로 전달
                req.setAttribute("authenticatedUserid", id);
                req.setAttribute("authenticatedRoles", roles);
            } else {
                res.setStatus(HttpStatus.UNAUTHORIZED.value());
                res.setContentType("application/json;charset=UTF-8");
                res.getWriter().write("{\"message\":\"권한 없음: 유효하지 않거나 토큰이 없습니다\"}");
                return;
            }
        } else {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"message\":\"권한 없음: 토큰이 없습니다\"}");
            return;
        }
        chain.doFilter(req, res);
    }
}

