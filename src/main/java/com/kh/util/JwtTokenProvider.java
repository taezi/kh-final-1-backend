package com.kh.util;

import com.kh.dto.MemberDTO;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}") private String secretKey;
    @Value("${jwt.access-token-expiration-ms}") private long accessMs;
    @Value("${jwt.refresh-token-expiration-ms}") private long refreshMs;


    private Key key;

    @PostConstruct
    public void init() { this.key = Keys.hmacShaKeyFor(secretKey.getBytes()); }

    public String generateAccessToken(MemberDTO user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserno()))
                .claim("roles", user.getRole())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(MemberDTO user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserno()))
                .claim("roles", user.getRole())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUseridFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    public String getRolesFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("roles", String.class);
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getRefreshTokenExpirationMs(){ return refreshMs; }
}
