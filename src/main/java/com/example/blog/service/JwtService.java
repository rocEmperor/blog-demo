package com.example.blog.service;

import com.example.blog.security.JwtUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Integer userId, String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 校验签名与有效期，解析出当前用户（供 JWT 过滤器使用）
     */
    public Optional<JwtUserPrincipal> parseToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey())
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody();
            Integer userId = Integer.parseInt(claims.getSubject());
            String username = claims.get("username", String.class);
            if (username == null) {
                return Optional.empty();
            }
            return Optional.of(new JwtUserPrincipal(userId, username));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<Integer> tryParseBearerUserId(HttpServletRequest request) {
        if (request == null) return Optional.empty();
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) return Optional.empty();
        return parseToken(header.substring(BEARER_PREFIX.length()).trim()).map(JwtUserPrincipal::getId);
    }
}
