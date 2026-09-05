package com.agribid.nexus.dto.response;

import com.agribid.nexus.domain.user.Role;

/**
 * Deliberately does NOT include a token field — this endpoint never
 * issues a new one. The client already holds a valid token (that's
 * how it authenticated to reach this endpoint at all); this just
 * answers "who does this token belong to," so a mobile app can
 * restore a session on cold start without asking the user to log in
 * again every single time.
 */
public record CurrentUserResponse(
    Long userId,
    String email,
    Role role,
    boolean kycVerified
) {
}