package dev.tuoi.customforms.model;

import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Question {
    private String id;
    private String title;
    private String description;
    private QuestionType type;
    private boolean required;
    private List<String> options;
}

