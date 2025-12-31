package dev.tuoi.customforms.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/** Utility class for JWT generation and validation */
@Component
public class JwtUtils {

    private final Key key;
    private final long expirationMs;

    /** Initializes signing key from secret and token expiration duration */
    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    /** Generates JWT containing userId as subject and email as claim */
    public String generateToken(String userId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Validates token signature and returns parsed claims; throws on invalid token */
    public Jws<Claims> validate(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    /** Extracts userId (subject) from validated token */
    public String getUserId(String token) {
        return validate(token).getBody().getSubject();
    }
}