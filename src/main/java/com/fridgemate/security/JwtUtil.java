package com.fridgemate.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Injected from application.yml → app.jwt.secret
    @Value("${app.jwt.secret}")
    private String secret;

    // Injected from application.yml → app.jwt.expiration-ms
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    // Derives a cryptographic signing key from the secret string.
    // HMAC-SHA256 requires at least 256 bits (32 bytes).
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Creates a signed JWT token for the given email.
    // Called once when the user successfully logs in.
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                                  // "sub" claim — who this token is for
                .issuedAt(new Date())                            // "iat" claim — when it was created
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // "exp" claim
                .signWith(getSigningKey())                       // signs the token with our secret
                .compact();                                      // serialises to the "xxx.yyy.zzz" string
    }

    // Extracts the email (subject) from a token.
    // Called by JwtAuthFilter on every protected request.
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // Returns true if the token signature is valid and it hasn't expired.
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date()); // check expiry
        } catch (Exception e) {
            // Any parsing or signature failure (tampered token, wrong key, expired) lands here
            return false;
        }
    }

    // Parses and verifies the token in one step.
    // Throws an exception if the signature is invalid or the token is malformed.
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())  // verifies the signature
                .build()
                .parseSignedClaims(token)
                .getPayload();               // returns the claims (subject, expiry, etc.)
    }
}
