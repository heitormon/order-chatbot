package dev.nobuo.order_service.application.usecases;

import dev.nobuo.order_service.application.exception.ConflictException;
import dev.nobuo.order_service.application.exception.InvalidInputException;
import dev.nobuo.order_service.application.port.inbound.CreateOrderUseCase;
import dev.nobuo.order_service.application.port.outbound.DateTimeProvider;
import dev.nobuo.order_service.application.port.outbound.OrderRepository;
import dev.nobuo.order_service.domain.Order;
import dev.nobuo.order_service.domain.OrderId;
import dev.nobuo.order_service.domain.exception.DomainValidationException;

import java.util.Objects;
import java.util.Optional;

public class CreateOrderService implements CreateOrderUseCase {
    private final OrderRepository orderRepository;
    private final DateTimeProvider dateTimeProvider;

    public CreateOrderService(OrderRepository orderRepository, DateTimeProvider dateTimeProvider) {
        this.orderRepository = orderRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public void execute(Input input) {
        try {
            createOrder(input);
        } catch (DomainValidationException ex) {
            throw new InvalidInputException(ex.getMessage(), ex);
        }
    }

    private void createOrder(Input input) {
        if (Objects.isNull(input)) {
            throw new InvalidInputException("input must not be null");
        }

        if (input.id() == null) {
            throw new InvalidInputException("id must not be null");
        }
        OrderId orderId = OrderId.of(input.id());

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            throw new ConflictException("order already exists");
        }

        Order order = Order.create(orderId, dateTimeProvider.now());
        orderRepository.save(order);
    }
}
