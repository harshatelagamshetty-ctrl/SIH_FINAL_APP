package com.agribid.nexus.controller;

import com.agribid.nexus.domain.contract.ForwardContract;
import com.agribid.nexus.domain.contract.Order;
import com.agribid.nexus.domain.user.Role;
import com.agribid.nexus.dto.response.MyContractSummaryResponse;
import com.agribid.nexus.dto.response.OrderResponse;
import com.agribid.nexus.repository.MyContractsRepository;
import com.agribid.nexus.repository.MyOrdersRepository;
import com.agribid.nexus.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Closes a real, honest gap: the app previously made a farmer or
 * distributor manually type a Contract ID or Order ID they had no
 * real way of knowing. This gives them the actual list to pick
 * from — role-aware, since a farmer's real contracts and a
 * distributor's real contracts are found via different relationship
 * paths through the same data.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MyTransactionsController {

    private final MyContractsRepository myContractsRepository;
    private final MyOrdersRepository myOrdersRepository;

    @GetMapping("/contracts/mine")
    public ResponseEntity<List<MyContractSummaryResponse>> myContracts(@AuthenticationPrincipal UserPrincipal principal) {
        List<ForwardContract> contracts = principal.getRole() == Role.FARMER
                ? myContractsRepository.findByFarmerId(principal.getId())
                : myContractsRepository.findByWinningDistributorId(principal.getId());

        List<MyContractSummaryResponse> response = contracts.stream()
                .map(c -> new MyContractSummaryResponse(
                        c.getId(),
                        c.getSourceListing().getCropLot().getCategory().getName(),
                        c.getLockedPrice(),
                        c.getStatus()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/mine")
    public ResponseEntity<List<OrderResponse>> myOrders(@AuthenticationPrincipal UserPrincipal principal) {
        List<Order> orders = principal.getRole() == Role.FARMER
                ? myOrdersRepository.findByFarmerId(principal.getId())
                : myOrdersRepository.findByWinningDistributorId(principal.getId());

        List<OrderResponse> response = orders.stream()
                .map(o -> new OrderResponse(
                        o.getId(),
                        o.getContract().getSourceListing().getCropLot().getCategory().getName(),
                        o.getContract().getSourceListing().getCropLot().getQuantityKg(),
                        o.getContract().getLockedPrice(),
                        o.getCreatedAt(),
                        o.getFulfillments().size()))
                .toList();
        return ResponseEntity.ok(response);
    }
}