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
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/place/**",
                                "/api/weather/**",
                                "/api/editor/**",
                                "/api/movies/**",
                                "/api/cinemas/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cafe/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rest/**").permitAll()
                        .requestMatchers("/api/ai/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/notices/**").permitAll() // GET만 허용 나머지는 관리자권한 필요
                        .requestMatchers("/api/notices/**", "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/editors/**").permitAll() // GET만 허용 나머지는 에디터권한 필요
                        .requestMatchers("/api/editors/**").hasRole("EDITOR")
                        .requestMatchers("/api/manage/inquiry/**").authenticated()  // 로그인한 유저만 사용가능
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