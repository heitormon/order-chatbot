package dev.nobuo.order_service.application.port.inbound;

public interface UpdateOrderUseCase {
    void execute(Input input);

    record Input(String id, String cancelReason) {
    }
}