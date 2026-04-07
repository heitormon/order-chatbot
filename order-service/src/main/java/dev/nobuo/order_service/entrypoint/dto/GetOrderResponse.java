package dev.nobuo.order_service.entrypoint.dto;

import java.time.Instant;

public record GetOrderResponse(String id, String status, Instant createdAt, Instant updatedAt) {
}
