package dev.nobuo.order_service.application.port.inbound;

import java.time.Instant;

public interface GetOrderUseCase {
    Output execute(Input input);

    record Input(String id) {
    }

    record Output(String id, String status, Instant createdAt, Instant updatedAt) {
    }
}