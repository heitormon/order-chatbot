package dev.nobuo.order_service.application.usecases;

import dev.nobuo.order_service.application.exception.ConflictException;
import dev.nobuo.order_service.application.exception.InvalidInputException;
import dev.nobuo.order_service.application.port.inbound.CreateOrderUseCase;
import dev.nobuo.order_service.application.port.outbound.DateTimeProvider;
import dev.nobuo.order_service.application.port.outbound.OrderRepository;
import dev.nobuo.order_service.domain.Order;
import dev.nobuo.order_service.domain.OrderId;
import dev.nobuo.order_service.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateOrderServiceTest {
    private CreateOrderUseCase.Input input;
    private OrderRepository orderRepository;
    private DateTimeProvider dateTimeProvider;

    private CreateOrderService createOrderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        dateTimeProvider = mock(DateTimeProvider.class);
        createOrderService = new CreateOrderService(orderRepository, dateTimeProvider);

        String id = "4d009b71-d6a8-4082-b5fc-a8672f8de9f1";
        Instant now = Instant.parse("2026-04-03T10:00:00Z");
        input = new CreateOrderUseCase.Input(id);

        OrderId orderId = OrderId.of(id);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        when(dateTimeProvider.now()).thenReturn(now);
    }

    @Test
    void createOrderWhenInputIsValid() {
        createOrderService.execute(input);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order order = orderCaptor.getValue();
        assertEquals(OrderId.of(input.id()), order.getId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(Instant.parse("2026-04-03T10:00:00Z"), order.getCreationDate());
        assertEquals(Instant.parse("2026-04-03T10:00:00Z"), order.getUpdateDate());
    }

    @Test
    void throwInvalidInputWhenInputIsNull() {
        InvalidInputException ex = assertThrowsExactly(
                InvalidInputException.class,
                () -> createOrderService.execute(null)
        );

        assertEquals("input must not be null", ex.getMessage());
        verifyNoInteractions(orderRepository, dateTimeProvider);
    }

    @Test
    void throwInvalidInputWhenIdIsNull() {
        input = new CreateOrderUseCase.Input(null);

        InvalidInputException ex = assertThrowsExactly(
                InvalidInputException.class,
                () -> createOrderService.execute(input)
        );

        assertEquals("id must not be null", ex.getMessage());
        verifyNoInteractions(orderRepository, dateTimeProvider);
    }

    @Test
    void throwInvalidInputWhenIdIsInvalid() {
        CreateOrderUseCase.Input input = new CreateOrderUseCase.Input("invalid-id");

        InvalidInputException ex = assertThrowsExactly(
                InvalidInputException.class,
                () -> createOrderService.execute(input)
        );

        assertEquals("id must be a valid UUID", ex.getMessage());
        verifyNoInteractions(orderRepository, dateTimeProvider);
    }

    @Test
    void throwConflictWhenOrderAlreadyExists() {
        OrderId orderId = OrderId.of(input.id());
        Order existingOrder = Order.create(orderId, Instant.parse("2026-04-03T10:00:00Z"));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        ConflictException ex = assertThrowsExactly(
                ConflictException.class,
                () -> createOrderService.execute(input)
        );

        assertEquals("order already exists", ex.getMessage());
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(dateTimeProvider);
    }
}
