package dev.tuoi.customforms.service;

import dev.tuoi.customforms.model.RefreshToken;
import dev.tuoi.customforms.repo.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/** Service for creating, verifying, and revoking refresh tokens. */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    private final long refreshTokenDurationMs = 7 * 24 * 60 * 60 * 1000L;

    /** Injects the refresh token repository. */
    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    /** Creates and persists a new refresh token (7-day expiry) for the given user. */
    public RefreshToken createToken(String userId) {
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();

        return repo.save(token);
    }

    /** Verifies token validity; throws RuntimeException if invalid, expired, or revoked. */
    public RefreshToken verify(String token) {
        RefreshToken t = repo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (t.isRevoked() || t.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }
        return t;
    }

    /** Marks an existing refresh token as revoked if present. */
    public void revoke(String token) {
        repo.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            repo.save(t);
        });
    }
}