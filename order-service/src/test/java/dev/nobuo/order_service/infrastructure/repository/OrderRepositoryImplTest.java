package dev.nobuo.order_service.infrastructure.repository;

import dev.nobuo.order_service.application.port.outbound.OrderRepository;
import dev.nobuo.order_service.domain.Order;
import dev.nobuo.order_service.domain.OrderId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

@Testcontainers
@SpringBootTest
class OrderRepositoryImplTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void savesOrder() {
        Instant now = Instant.now();
        OrderId orderId = OrderId.of("55fa4ba8-2b1e-4525-be6e-9389efa8416b");
        Order order = Order.create(orderId, now);
        orderRepository.save(order);

        orderRepository.findById(orderId).ifPresent(System.out::println);
    }
}