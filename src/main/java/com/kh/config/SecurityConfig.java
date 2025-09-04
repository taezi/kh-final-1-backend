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

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. 모든 GET 요청에 대한 permitAll 규칙들을 먼저 배치
                        .requestMatchers(HttpMethod.GET,
                                "/api/movie/**",
                                "/api/notices/**",
                                "/api/editors/**",
                                "/api/place/**",
                                "/api/weather/**",
                                "/api/cinemas/**"
                        ).permitAll()

                        // 2. 인증이 필요한 POST 요청을 구체적으로 명시
                        .requestMatchers(HttpMethod.POST, "/api/movie/review").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll() // 로그인/회원가입 등

                        // 3. 특정 역할이 필요한 규칙 (관리자, 에디터)
                        .requestMatchers("/api/notices/**", "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/editors/**").hasRole("EDITOR")
                        .requestMatchers(
                                "/api/manage/inquiry/**",
                                "/api/bookmarks/**").authenticated()  // 로그인한 유저만 사용가능
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요

                )
                .cors(Customizer.withDefaults())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정(React 개발 서버)
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${cors.allowed-origin}") String allowedOrigin) {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(allowedOrigin));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}