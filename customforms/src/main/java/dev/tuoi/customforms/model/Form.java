package dev.tuoi.customforms.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "forms")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Form {
    @Id
    private String id;

    private String ownerId;
    private String title;
    private String description;
    private boolean isPublic;
    private Instant createdAt;
    private Instant updatedAt;

    private List<Question> questions;
}
