package dev.tuoi.customforms.controller;

import dev.tuoi.customforms.dto.AuthRequest;
import dev.tuoi.customforms.dto.AuthResponse;
import dev.tuoi.customforms.dto.SignupRequest;
import dev.tuoi.customforms.dto.UserResponse;
import dev.tuoi.customforms.model.RefreshToken;
import dev.tuoi.customforms.model.User;
import dev.tuoi.customforms.service.AuthService;
import dev.tuoi.customforms.service.RefreshTokenService;
import dev.tuoi.customforms.config.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/** Authentication controller handling signup, login, user info, and token refresh. */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwt;

    /** Injects required services and JWT utilities. */
    public AuthController(AuthService authService,
                          RefreshTokenService refreshTokenService,
                          JwtUtils jwt) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwt = jwt;
    }

    /** Registers new user, issues access token and sets refresh token cookie */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest req, HttpServletResponse response) {
        String accessToken = authService.signup(req);
        User user = authService.getUserByEmail(req.getEmail());
        addRefreshTokenCookie(response, refreshTokenService.createToken(user.getId()).getToken());
        return ResponseEntity.ok(new AuthResponse(accessToken));
    }

    /** Authenticates user, issues access token and sets refresh token cookie on success */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req, HttpServletResponse response) {
        String accessToken = authService.login(req);  // throws ApiException on failure
        User user = authService.getUserByEmail(req.getEmail());
        addRefreshTokenCookie(response, refreshTokenService.createToken(user.getId()).getToken());
        return ResponseEntity.ok(new AuthResponse(accessToken));
    }

    /** Returns current authenticated user's information */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = authService.getMe(userId);
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getRole()));
    }

    /** Refreshes access token using refresh token cookie; issues new refresh token */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RefreshToken token;
        try {
            token = refreshTokenService.verify(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = authService.getMe(token.getUserId());
        String newAccessToken = jwt.generateToken(user.getId(), user.getEmail());

        refreshTokenService.revoke(refreshToken);
        String newRefreshToken = refreshTokenService.createToken(user.getId()).getToken();
        addRefreshTokenCookie(response, newRefreshToken);

        return ResponseEntity.ok(new AuthResponse(newAccessToken));
    }

    /** Sets HttpOnly refresh token cookie (7-day expiry) */
    private void addRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}