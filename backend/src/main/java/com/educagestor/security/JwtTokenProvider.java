package com.educagestor.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Token Provider for generating and validating JWT tokens
 * 
 * Handles creation, validation, and parsing of JWT access and refresh tokens
 * for user authentication and authorization.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    /**
     * Constructor to initialize JWT configuration
     * 
     * @param jwtSecret the JWT secret key
     * @param jwtExpiration the access token expiration time in milliseconds
     * @param jwtRefreshExpiration the refresh token expiration time in milliseconds
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpiration,
            @Value("${jwt.refresh-expiration}") long jwtRefreshExpiration) {
        
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.accessTokenExpirationTime = jwtExpiration;
        this.refreshTokenExpirationTime = jwtRefreshExpiration;
        
        log.info("JWT Token Provider initialized with expiration times - Access: {}ms, Refresh: {}ms", 
                jwtExpiration, jwtRefreshExpiration);
    }

    /**
     * Generate access token for authenticated user
     * 
     * @param authentication the authentication object
     * @return JWT access token
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + accessTokenExpirationTime);

        String token = Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("type", "access")
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        log.debug("Generated access token for user: {}", userPrincipal.getUsername());
        return token;
    }

    /**
     * Generate refresh token for authenticated user
     * 
     * @param authentication the authentication object
     * @return JWT refresh token
     */
    public String generateRefreshToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + refreshTokenExpirationTime);

        String token = Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("type", "refresh")
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        log.debug("Generated refresh token for user: {}", userPrincipal.getUsername());
        return token;
    }

    /**
     * Get username from JWT token
     * 
     * @param token the JWT token
     * @return username from token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Get token type from JWT token
     * 
     * @param token the JWT token
     * @return token type (access or refresh)
     */
    public String getTokenType(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("type", String.class);
    }

    /**
     * Get expiration date from JWT token
     * 
     * @param token the JWT token
     * @return expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }

    /**
     * Validate JWT token
     * 
     * @param token the JWT token to validate
     * @return true if token is valid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            
            log.debug("JWT token is valid");
            return true;
            
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        
        return false;
    }

    /**
     * Check if token is expired
     * 
     * @param token the JWT token
     * @return true if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Validate access token specifically
     * 
     * @param token the JWT token
     * @return true if token is a valid access token
     */
    public boolean validateAccessToken(String token) {
        if (!validateToken(token)) {
            return false;
        }
        
        String tokenType = getTokenType(token);
        return "access".equals(tokenType);
    }

    /**
     * Validate refresh token specifically
     * 
     * @param token the JWT token
     * @return true if token is a valid refresh token
     */
    public boolean validateRefreshToken(String token) {
        if (!validateToken(token)) {
            return false;
        }
        
        String tokenType = getTokenType(token);
        return "refresh".equals(tokenType);
    }

    /**
     * Get access token expiration time in milliseconds
     * 
     * @return expiration time in milliseconds
     */
    public long getAccessTokenExpirationTime() {
        return accessTokenExpirationTime;
    }

    /**
     * Get refresh token expiration time in milliseconds
     * 
     * @return expiration time in milliseconds
     */
    public long getRefreshTokenExpirationTime() {
        return refreshTokenExpirationTime;
    }

    /**
     * Get remaining time until token expires
     * 
     * @param token the JWT token
     * @return remaining time in milliseconds, or 0 if expired
     */
    public long getRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remaining);
        } catch (Exception e) {
            log.error("Error calculating remaining time: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Extract token from Authorization header
     * 
     * @param authHeader the Authorization header value
     * @return JWT token without Bearer prefix, or null if invalid
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
