package dev.nobuo.order_service.application.port.inbound;

public interface CreateOrderUseCase {
    void execute(Input input);

    record Input(String id) {
    }
}