package com.agribid.nexus.domain.wallet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A real, working ledger — but a deliberately SIMULATED one. This
 * project explicitly does not integrate a real payment provider
 * (Stripe, Razorpay, or otherwise) — per direct instruction, no real
 * money ever moves. "Top up" simply increases this stored balance
 * directly. What IS real: the balance genuinely gates bidding
 * (see BidListingServiceImpl wiring instructions), so the mechanism
 * being demonstrated — "you need funds on file before you can bid"
 * — is functionally real, even though the funding source is not.
 */
@Entity
@Table(name = "wallets", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Wallet(Long userId) {
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
    }
}
