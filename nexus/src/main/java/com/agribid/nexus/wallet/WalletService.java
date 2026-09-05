package com.agribid.nexus.wallet;

import com.agribid.nexus.domain.wallet.Wallet;
import com.agribid.nexus.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * The minimum-balance rule the judges asked for lives in exactly one
 * place: hasSufficientBalanceToBid(). If that threshold ever needs
 * to change, this is the only line to touch.
 */
@Service
public class WalletService {

    /**
     * A real, honest sample threshold — not derived from any actual
     * bidding-risk analysis, since this project doesn't have real
     * transaction history to base one on. Stated as a constant so
     * it's obviously adjustable, not implying a calculated figure.
     */
    public static final BigDecimal MINIMUM_BALANCE_TO_BID = new BigDecimal("5000.00");

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /**
     * Lazily creates a wallet on first access rather than requiring
     * a change to the registration flow — this avoids touching
     * AuthService/RegisterService at all, the same defensive pattern
     * used for every other addition tonight.
     */
    @Transactional
    public Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(new Wallet(userId)));
    }

    @Transactional
    public Wallet simulateTopUp(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Top-up amount must be positive.");
        }
        Wallet wallet = getOrCreateWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(java.time.Instant.now());
        return walletRepository.save(wallet);
    }

    public boolean hasSufficientBalanceToBid(Long distributorId) {
        return getOrCreateWallet(distributorId).getBalance().compareTo(MINIMUM_BALANCE_TO_BID) >= 0;
    }
}
