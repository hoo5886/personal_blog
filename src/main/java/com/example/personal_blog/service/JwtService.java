package com.example.personal_blog.service;

import com.example.personal_blog.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtService {

    @Setter
    @Value("${spring.token.signing.key}")
    private String jwtSigningKey;

    @Value("${spring.token.signing.expiration}")
    private long expiration;


    /**
     * Extracts the loginId from the token
     * @param token
     * @return
     */
    public String extractLoginId(String token) {
        return extractClaim(token, claims -> {
            log.debug("All claims in token: {}", claims);

            String loginId = claims.get("loginId", String.class);
            if (loginId != null) {
                log.debug("LoginId claim found: {}", loginId);
                return loginId;
            }

            String subject = claims.getSubject();
            if (subject != null) {
                log.debug("Subject claim used as loginId: {}", subject);
                return subject;
            }

            log.warn("No loginId or subject found in token");
            return null;
        });
    }

    public String generateToken(User user) {
        log.info("Generating JWT for user2: {}", user.getUsername());
        return generateToken(new HashMap<>(), user);
    }

    private String generateToken(Map<String, Object> extraClaims, User user) {
        log.info("Generating JWT for user: {}", user.getUsername());

        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("sub", user.getLoginId());  // Set subject as loginId
        claims.put("loginId", user.getLoginId());  // Ensure loginId is always set
        claims.put("username", user.getUsername());  // Add username as a separate claim if needed

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String loginId = extractLoginId(token);
            boolean isValid = (loginId != null && loginId.equals(userDetails.getUsername()))
                && !isTokenExpired(token);
            log.info("Validating JWT for user: {} - valid: {}", loginId, isValid);

            return isValid;
        } catch (Exception e) {
            log.error("Error validating token", e);
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            log.error("Error parsing token: {}", token, e);
            throw e;
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}