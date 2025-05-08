package com.hy.security_common.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.validity}")
    private long validityInMilliseconds;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Long id, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getKey())
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String username = extractUsername(token);
        return new UsernamePasswordAuthenticationToken(username, "", List.of());
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Long getIdFromToken(String token) {
        Claims claims = getClaims(token);
        String id = claims.get("id").toString();
        return Long.parseLong(id);
    }


    public String getRole(String token) {
        Claims claims = getClaims(token);
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.split(" ")[1].trim();
        }
        return Jwts.parserBuilder().setSigningKey(getKey())
                .build().parseClaimsJws(token).getBody();
    }
}
