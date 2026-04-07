package dev.nobuo.order_service.application.usecases;

import dev.nobuo.order_service.application.exception.InvalidInputException;
import dev.nobuo.order_service.application.exception.NotFoundException;
import dev.nobuo.order_service.application.port.inbound.GetOrderUseCase;
import dev.nobuo.order_service.application.port.outbound.OrderRepository;
import dev.nobuo.order_service.domain.Order;
import dev.nobuo.order_service.domain.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetOrderUseCaseServiceTest {
    private GetOrderUseCase.Input input;
    private OrderRepository orderRepository;

    private GetOrderUseCaseService service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);

        service = new GetOrderUseCaseService(orderRepository);

        String id = "4d009b71-d6a8-4082-b5fc-a8672f8de9f1";
        OrderId orderId = OrderId.of(id);
        Instant now = Instant.parse("2018-01-01T00:00:00.00Z");
        Order order = Order.create(orderId, now);
        input = new GetOrderUseCase.Input(id);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    }

    @Test
    void success() {
        GetOrderUseCase.Output output = service.execute(input);

        assertEquals("4d009b71-d6a8-4082-b5fc-a8672f8de9f1", output.id());
        assertEquals(Instant.parse("2018-01-01T00:00:00.00Z"), output.createdAt());
        assertEquals(Instant.parse("2018-01-01T00:00:00.00Z"), output.updatedAt());
        assertEquals("CREATED", output.status());
    }

    @Test
    void throwInvalidInputIsNull() {
        input = null;

        InvalidInputException invalidInputException = assertThrowsExactly(InvalidInputException.class, () -> service.execute(input));
        assertEquals("input must not be null", invalidInputException.getMessage());
    }

    @Test
    void throwInvalidInputWhenIdIsNull() {
        input = new GetOrderUseCase.Input(null);

        InvalidInputException invalidInputException = assertThrowsExactly(InvalidInputException.class, () -> service.execute(input));
        assertEquals("id must not be null", invalidInputException.getMessage());
    }

    @Test
    void throwInvalidInputWhenInvalidId() {
        input = new GetOrderUseCase.Input("invalid-id");

        InvalidInputException invalidInputException = assertThrowsExactly(InvalidInputException.class, () -> service.execute(input));
        assertEquals("id must be a valid UUID", invalidInputException.getMessage());
    }

    @Test
    void throwNotFoundWhenOrderIsEmpty() {
        when(orderRepository.findById(OrderId.of(input.id()))).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrowsExactly(NotFoundException.class, () -> service.execute(input));
        assertEquals("Order with id 4d009b71-d6a8-4082-b5fc-a8672f8de9f1 not found", notFoundException.getMessage());
    }


}