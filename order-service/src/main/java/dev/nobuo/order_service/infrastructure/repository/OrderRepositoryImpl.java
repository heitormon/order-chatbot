package dev.nobuo.order_service.infrastructure.repository;

import dev.nobuo.order_service.application.port.outbound.OrderRepository;
import dev.nobuo.order_service.domain.Order;
import dev.nobuo.order_service.domain.OrderId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaOrderRepository jpaOrderRepository;

    public OrderRepositoryImpl(JpaOrderRepository jpaOrderRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return jpaOrderRepository.findById(orderId);
    }

    @Override
    public void save(Order order) {
        jpaOrderRepository.save(order);
    }
}
