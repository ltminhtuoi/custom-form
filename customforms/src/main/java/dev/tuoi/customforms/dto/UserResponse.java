package dev.tuoi.customforms.dto;

import dev.tuoi.customforms.model.Role;

public record UserResponse(
        String id,
        String email,
        Role role
) {}