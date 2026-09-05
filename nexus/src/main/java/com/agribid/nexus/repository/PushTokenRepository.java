package com.agribid.nexus.repository;

import com.agribid.nexus.domain.notification.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    List<PushToken> findByUserId(Long userId);
    Optional<PushToken> findByExpoPushToken(String expoPushToken);
}