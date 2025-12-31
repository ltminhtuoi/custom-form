package dev.tuoi.customforms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class SubmitResponseRequest {
    private String responderEmail;
    private Map<String,Object> answers;
}
