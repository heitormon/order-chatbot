package dev.nobuo.order_service.application.usecases;

import dev.nobuo.order_service.application.exception.InvalidInputException;
import dev.nobuo.order_service.application.exception.NotFoundException;
import dev.nobuo.order_service.application.port.inbound.GetOrderUseCase;
import dev.nobuo.order_service.application.port.outbound.OrderRepository;
import dev.nobuo.order_service.domain.Order;
import dev.nobuo.order_service.domain.OrderId;
import dev.nobuo.order_service.domain.exception.DomainValidationException;
import org.springframework.stereotype.Service;

@Service
public class GetOrderUseCaseService implements GetOrderUseCase {
    private final OrderRepository orderRepository;

    public GetOrderUseCaseService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Output execute(Input input) {
        try {
            return getOrder(input);
        } catch (DomainValidationException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    private Output getOrder(Input input) {
        if (input == null) {
            throw new InvalidInputException("input must not be null");
        }
        if (input.id() == null) {
            throw new InvalidInputException("id must not be null");
        }
        OrderId orderId = OrderId.of(input.id());
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException("Order with id " + orderId + " not found"));

        return new Output(order.getId().getValue(),
                order.getStatus().name(),
                order.getCreationDate(),
                order.getUpdateDate());
    }
}
