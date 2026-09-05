package com.agribid.nexus.controller;

import com.agribid.nexus.domain.wallet.Wallet;
import com.agribid.nexus.security.UserPrincipal;
import com.agribid.nexus.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    public record TopUpRequest(BigDecimal amount) {}

    @GetMapping("/mine")
    public ResponseEntity<Map<String, Object>> myWallet(@AuthenticationPrincipal UserPrincipal principal) {
        Wallet wallet = walletService.getOrCreateWallet(principal.getId());
        return ResponseEntity.ok(Map.of(
                "balance", wallet.getBalance(),
                "minimumBalanceToBid", WalletService.MINIMUM_BALANCE_TO_BID,
                "canBid", wallet.getBalance().compareTo(WalletService.MINIMUM_BALANCE_TO_BID) >= 0,
                "note", "This is a simulated wallet — no real payment provider is connected. No real money moves."
        ));
    }

    @PostMapping("/top-up")
    public ResponseEntity<Map<String, Object>> topUp(@RequestBody TopUpRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        Wallet wallet = walletService.simulateTopUp(principal.getId(), request.amount());
        return ResponseEntity.ok(Map.of(
                "balance", wallet.getBalance(),
                "note", "Simulated top-up applied — no real payment was processed."
        ));
    }
}
