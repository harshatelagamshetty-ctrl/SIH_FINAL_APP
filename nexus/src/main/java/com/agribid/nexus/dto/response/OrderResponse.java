package com.agribid.nexus.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
    Long orderId,
    String cropCategory,
    java.math.BigDecimal quantityKg,
    BigDecimal lockedPricePerKg,
    Instant createdAt,
    int fulfillmentCount
) {
}