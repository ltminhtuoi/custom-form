package dev.tuoi.customforms.service;

import dev.tuoi.customforms.dto.AuthRequest;
import dev.tuoi.customforms.dto.SignupRequest;
import dev.tuoi.customforms.common.ApiException;
import dev.tuoi.customforms.config.JwtUtils;
import dev.tuoi.customforms.model.Role;
import dev.tuoi.customforms.model.User;
import dev.tuoi.customforms.repo.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/** Service handling user signup, login, and basic retrieval. */
@Service
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtils jwt;

    /** Injects user repository and JWT utilities. */
    public AuthService(UserRepository userRepo, JwtUtils jwt) {
        this.userRepo = userRepo;
        this.jwt = jwt;
    }

    /** Registers a new user and returns an access token. */
    public String signup(SignupRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ApiException("Email already registered", 400);
        }
        String hash = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt(12));
        User user = User.builder()
                .email(req.getEmail())
                .passwordHash(hash)
                .role(Role.USER)
                .build();
        userRepo.save(user);
        return jwt.generateToken(user.getId(), user.getEmail());
    }

    /** Authenticates user by email/password and returns an access token. */
    public String login(AuthRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException("Invalid credentials", 401));
        if (!BCrypt.checkpw(req.getPassword(), user.getPasswordHash())) {
            throw new ApiException("Invalid credentials", 401);
        }
        return jwt.generateToken(user.getId(), user.getEmail());
    }

    /** Retrieves authenticated user by ID. */
    public User getMe(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", 404));
    }

    /** Retrieves user by email address. */
    public User getUserByEmail(@Email @NotBlank String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", 404));
    }
}