package dev.nobuo.order_service.domain;

import dev.nobuo.order_service.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static dev.nobuo.order_service.domain.OrderStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void create_success() {
        OrderId orderId = OrderId.of("4d009b71-d6a8-4082-b5fc-a8672f8de9f1");
        Instant creationDate = Instant.parse("2018-09-09T00:00:00Z");

        Order order = Order.create(orderId, creationDate);

        assertEquals(CREATED, order.getStatus());
        assertEquals(orderId, order.getId());
        assertEquals(creationDate, order.getCreationDate());
        assertEquals(creationDate, order.getUpdateDate());
        assertNull(order.getCancellationReason());
    }

    @Test
    void create_failure() {
        Instant creationDate = Instant.parse("2018-09-09T00:00:00Z");
        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> Order.create(null, creationDate));
        assertEquals("id must not be null", domainValidationException.getMessage());

        OrderId orderId = OrderId.of("4d009b71-d6a8-4082-b5fc-a8672f8de9f1");
        domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> Order.create(orderId, null));
        assertEquals("creationDate must not be null", domainValidationException.getMessage());
    }

    @Test
    void complete_success() {
        Order order = createOrder();
        Instant updateDate = Instant.parse("2018-09-10T00:00:00Z");

        order.confirm(updateDate);

        assertEquals(CONFIRMED, order.getStatus());
        assertEquals(updateDate, order.getUpdateDate());
    }

    @Test
    void complete_throwsException_whenOrderIsNotCreated() {
        Order order = createOrder();
        order.confirm(Instant.parse("2018-09-10T00:00:00Z"));

        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> order.confirm(Instant.parse("2018-09-10T00:00:00Z")));
        assertEquals("Cannot confirm an order that is not in CREATED status", domainValidationException.getMessage());
    }

    @Test
    void complete_throwException_whenInputIsNull() {
        Order order = createOrder();

        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> order.confirm(null));
        assertEquals("updateDate must not be null", domainValidationException.getMessage());
    }

    @Test
    void cancel_success() {
        Order order = createOrder();
        order.confirm(Instant.parse("2018-09-10T00:00:00Z"));
        Instant cancelDate = Instant.parse("2018-09-11T00:00:00Z");

        order.cancel(cancelDate, "Cancelled");

        assertEquals(CANCELLED, order.getStatus());
        assertEquals(cancelDate, order.getUpdateDate());
        assertEquals("Cancelled", order.getCancellationReason());
    }

    @Test
    void cancel_throwsException_whenOrderIsNotConfirmed() {
        Order order = createOrder();

        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> order.cancel(Instant.parse("2018-09-10T00:00:00Z"), "Cancelled"));
        assertEquals("Cannot cancel an order that is not in CONFIRMED status", domainValidationException.getMessage());
    }

    @Test
    void cancel_throwsException_whenInputIsNull() {
        Order order = createOrder();
        order.confirm(Instant.parse("2018-09-10T00:00:00Z"));

        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> order.cancel(null, "Cancelled"));
        assertEquals("updateDate must not be null", domainValidationException.getMessage());

        domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> order.cancel(Instant.parse("2018-09-10T00:00:00Z"), null));
        assertEquals("cancellationReason must not be null or blank", domainValidationException.getMessage());

    }

    @Test
    void ship_success() {
        Order order = createOrder();
        order.confirm(Instant.parse("2018-09-10T00:00:00Z"));
        Instant shipDate = Instant.parse("2018-09-11T00:00:00Z");

        order.ship(shipDate);

        assertEquals(SHIPPED, order.getStatus());
        assertEquals(shipDate, order.getUpdateDate());
    }

    @Test
    void ship_throwsException_whenOrderIsNotConfirmed() {
        Order order = createOrder();

        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> order.ship(Instant.parse("2018-09-10T00:00:00Z")));
        assertEquals("Cannot ship an order that is not in CONFIRMED status", domainValidationException.getMessage());
    }

    @Test
    void receive_success() {
        Order order = createOrder();
        order.confirm(Instant.parse("2018-09-10T00:00:00Z"));
        order.ship(Instant.parse("2018-09-11T00:00:00Z"));
        Instant receiveDate = Instant.parse("2018-09-12T00:00:00Z");

        order.receive(receiveDate);

        assertEquals(RECEIVED, order.getStatus());
        assertEquals(receiveDate, order.getUpdateDate());
    }

    @Test
    void receive_throwsException_whenOrderIsNotShipped() {
        Order order = createOrder();
        order.confirm(Instant.parse("2018-09-10T00:00:00Z"));

        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> order.receive(Instant.parse("2018-09-11T00:00:00Z")));
        assertEquals("Cannot receive an order that is not in SHIPPED status", domainValidationException.getMessage());
    }

    @Test
    void update_throwsException_whenUpdateDateIsBeforeCreationDate() {
        Order order = createOrder();

        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> order.confirm(Instant.parse("2018-09-08T00:00:00Z")));
        assertEquals("updateDate must be greater than or equal to creationDate", domainValidationException.getMessage());
    }

    @Test
    void update_throwsException_whenUpdateDateIsBeforeCurrentUpdateDate() {
        Order order = createOrder();
        order.confirm(Instant.parse("2018-09-10T00:00:00Z"));

        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> order.ship(Instant.parse("2018-09-09T12:00:00Z")));
        assertEquals("updateDate must be greater than or equal to current updateDate", domainValidationException.getMessage());
    }

    private static Order createOrder() {
        OrderId orderId = OrderId.of("4d009b71-d6a8-4082-b5fc-a8672f8de9f1");
        Instant creationDate = Instant.parse("2018-09-09T00:00:00Z");

        return Order.create(orderId, creationDate);
    }

}
