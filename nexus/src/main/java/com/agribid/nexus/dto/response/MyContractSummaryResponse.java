package com.agribid.nexus.dto.response;

import com.agribid.nexus.domain.contract.ContractStatus;

import java.math.BigDecimal;

/**
 * Deliberately separate from the existing ForwardContractResponse
 * (used by GET /contracts/{id}) rather than changing its shape,
 * which could break other callers already depending on it. This one
 * exists specifically for the "my contracts" picker list, matching
 * exactly the fields that screen actually displays.
 */
public record MyContractSummaryResponse(
    Long contractId,
    String cropCategory,
    BigDecimal lockedPricePerKg,
    ContractStatus status
) {
}