package com.agribid.nexus.controller;

import com.agribid.nexus.domain.notification.PushToken;
import com.agribid.nexus.repository.PushTokenRepository;
import com.agribid.nexus.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Matches the exact path the mobile app's pushNotifications.ts
 * already calls: POST /api/v1/notifications/register-token.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class PushTokenController {

    private final PushTokenRepository pushTokenRepository;

    public record RegisterTokenRequest(String expoPushToken) {}

    @PostMapping("/register-token")
    public ResponseEntity<?> registerToken(@RequestBody RegisterTokenRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        // Upsert by token, not by user — the unique constraint is on
        // the token itself (see PushToken's Javadoc for why), so a
        // re-registration from the same device updates the existing
        // row's owner rather than creating a duplicate.
        PushToken token = pushTokenRepository.findByExpoPushToken(request.expoPushToken())
                .orElseGet(() -> new PushToken(principal.getId(), request.expoPushToken()));
        token.setUserId(principal.getId());
        token.setUpdatedAt(java.time.Instant.now());
        pushTokenRepository.save(token);
        return ResponseEntity.ok(Map.of("registered", true));
    }
}