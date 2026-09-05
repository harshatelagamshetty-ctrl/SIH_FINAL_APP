package com.agribid.nexus.controller;

import com.agribid.nexus.dto.request.AuthRequest;
import com.agribid.nexus.dto.request.RegisterRequest;
import com.agribid.nexus.dto.response.AuthResponse;
import com.agribid.nexus.dto.response.CurrentUserResponse;
import com.agribid.nexus.security.UserPrincipal;
import com.agribid.nexus.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Session restore for a mobile app: on cold start, if a token is
     * already stored on-device, the app calls this instead of
     * forcing a fresh login. Built directly from UserPrincipal's
     * already-parsed JWT claims — no extra database query needed.
     * If the token is expired or invalid, this never executes at
     * all; JwtAuthFilter rejects the request before it reaches here,
     * and the app's own 401 handling should treat that as "session
     * expired, please log in again."
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(new CurrentUserResponse(
                principal.getId(), principal.getEmail(), principal.getRole(), principal.isKycVerified()));
    }
}
