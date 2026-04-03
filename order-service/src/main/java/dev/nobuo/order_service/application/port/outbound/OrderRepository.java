package dev.nobuo.order_service.application.port.outbound;

import dev.nobuo.order_service.domain.Order;
import dev.nobuo.order_service.domain.OrderId;

import java.util.Optional;

public interface OrderRepository {

    Optional<Order> findById(OrderId orderId);

    void save(Order order);
}
