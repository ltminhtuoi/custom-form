package dev.tuoi.customforms.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "responses")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ResponseEntry {
    @Id
    private String id;

    private String formId;
    private Instant submittedAt;

    private String responderEmail;

    private Map<String, Object> answers;
}
