package dev.tuoi.customforms.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("refresh_tokens")
public class RefreshToken {
    @Id
    private String id;
    private String userId;
    private String token;
    private Instant expiresAt;
    private boolean revoked;
}
