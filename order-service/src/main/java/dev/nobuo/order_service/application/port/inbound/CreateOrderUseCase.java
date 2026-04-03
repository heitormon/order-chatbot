package dev.nobuo.order_service.application.port.inbound;

public interface CreateOrderUseCase {
    record Input(String id) {
    }

    void execute(Input input);
}