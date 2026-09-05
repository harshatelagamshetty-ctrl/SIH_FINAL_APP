package com.agribid.nexus.domain.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * One row per (user, device). A user could plausibly register the
 * same account on more than one phone, and each phone gets its own
 * Expo push token — the unique constraint is on the token itself,
 * not the user, so re-registering the same physical device after a
 * reinstall correctly updates the existing row instead of creating
 * a duplicate.
 */
@Entity
@Table(name = "push_tokens", uniqueConstraints = @UniqueConstraint(columnNames = "expo_push_token"))
@Getter
@Setter
@NoArgsConstructor
public class PushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expo_push_token", nullable = false, length = 200)
    private String expoPushToken;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public PushToken(Long userId, String expoPushToken) {
        this.userId = userId;
        this.expoPushToken = expoPushToken;
    }
}