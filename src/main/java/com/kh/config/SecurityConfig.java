// src/main/java/com/kh/config/SecurityConfig.java
package com.kh.config;

import com.kh.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        /* ====== 공개 경로 (순서 최상단, 상세도 포함) ====== */
                        .requestMatchers("/api/place/**").permitAll()
                        .requestMatchers("/api/weather/**").permitAll()

                        /* ====== 기타 공개 GET ====== */
                        .requestMatchers(HttpMethod.GET,
                                "/api/movie/**",
                                "/api/notices/**",
                                "/api/editors/**",
                                "/api/cinemas/**"
                        ).permitAll()

                        /* ====== 회원 관련 공개 ====== */
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/manage/find-id").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/manage/find-pwd").permitAll()

                        /* ====== 인증 필요 API ====== */
                        .requestMatchers(HttpMethod.POST, "/api/movie/review").authenticated()
                        .requestMatchers("/api/manage/inquiry/**", "/api/bookmarks/**").authenticated()

                        /* ====== 권한 필요 ====== */
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/editors/**").hasRole("EDITOR")

                        /* ====== 나머지 ====== */
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            // 여러 포트를 허용 (기본값: 3000, 5173, 9999)
            @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:9999}") String origins) {
        CorsConfiguration cfg = new CorsConfiguration();

        // 콤마로 구분된 문자열을 리스트로 변환
        List<String> list = Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        cfg.setAllowedOrigins(list); // 정확한 도메인 허용
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
