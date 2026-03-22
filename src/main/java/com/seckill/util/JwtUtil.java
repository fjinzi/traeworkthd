package com.seckill.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:seckill-system-secret-key-for-jwt-signing}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username, Integer role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token已过期");
            throw new RuntimeException("登录已过期，请重新登录");
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT token");
            throw new RuntimeException("无效的登录凭证");
        } catch (MalformedJwtException e) {
            log.warn("JWT token格式错误");
            throw new RuntimeException("无效的登录凭证");
        } catch (SecurityException e) {
            log.warn("JWT token签名验证失败");
            throw new RuntimeException("无效的登录凭证");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token为空或非法");
            throw new RuntimeException("无效的登录凭证");
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.getSubject());
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public Integer getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", Integer.class);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Data
    public static class TokenInfo {
        private Long userId;
        private String username;
        private Integer role;
    }

    public TokenInfo getTokenInfo(String token) {
        Claims claims = parseToken(token);
        TokenInfo info = new TokenInfo();
        info.setUserId(Long.valueOf(claims.getSubject()));
        info.setUsername(claims.get("username", String.class));
        info.setRole(claims.get("role", Integer.class));
        return info;
    }
}
