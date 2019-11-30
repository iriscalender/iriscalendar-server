package com.javaproject.iriscalendar.service.auth;

import com.javaproject.iriscalendar.exception.InvalidJwtAuthenticationException;
import com.javaproject.iriscalendar.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {
    public String createToken(String userID) {
        String key = "key";

        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<>();
        long expiredTime = 1000 * 60 * 60 * 24 * 7L;

        Date now = new Date();
        now.setTime(now.getTime() + expiredTime);
        payloads.put("exp", now);
        payloads.put("data", userID);

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
    }

    public String getIdentity(String auth) {
        String jwt = auth.replace("Bearer ", "");
        try {
            Claims parsed = Jwts.parser()
                    .setSigningKey("key".getBytes())
                    .parseClaimsJws(jwt)
                    .getBody();
            if (parsed.getExpiration().before(Date.from(Instant.now()))) {
                throw new TokenExpiredException();
            }
            return parsed.get("data", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }
    }
}

