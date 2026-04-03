package dev.nobuo.order_service.infrastructure.repository;


import dev.nobuo.order_service.domain.Order;
import dev.nobuo.order_service.domain.OrderId;
import org.springframework.data.repository.ListCrudRepository;

public interface JpaOrderRepository extends ListCrudRepository<Order, OrderId>{
}
