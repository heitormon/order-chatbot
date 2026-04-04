package dev.nobuo.order_service.domain;

import dev.nobuo.order_service.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderIdTest {

    @Test
    void createValueOf_success() {
        String uuid = "4d009b71-d6a8-4082-b5fc-a8672f8de9f1";
        OrderId orderId = OrderId.of(uuid);

        assertEquals(uuid, orderId.getValue());
    }

    @Test
    void createValueOf_throwException_whenInputIsInvalid() {
        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> OrderId.of("invalid"));
        assertEquals("id must be a valid UUID", domainValidationException.getMessage());
    }

    @Test
    void createValueOf_throwException_whenInputIsNull() {
        DomainValidationException domainValidationException = assertThrowsExactly(DomainValidationException.class, () -> OrderId.of(null));
        assertEquals("id must not be null", domainValidationException.getMessage());
    }

    @Test
    void equalsNull_returnsFalse() {
        OrderId orderId = OrderId.of("4d009b71-d6a8-4082-b5fc-a8672f8de9f1");
        assertNotEquals(null, orderId);
        assertNotEquals(new Object(), orderId);
    }

    @Test
    void equalsSameValue_returnsTrue() {
        OrderId orderId = OrderId.of("4d009b71-d6a8-4082-b5fc-a8672f8de9f1");
        OrderId orderId2 = OrderId.of("4d009b71-d6a8-4082-b5fc-a8672f8de9f1");
        assertEquals(orderId, orderId2);
    }

    @Test
    void hashCode_returnIdHashCode() {
        String uuid = "4d009b71-d6a8-4082-b5fc-a8672f8de9f1";
        OrderId orderId = OrderId.of(uuid);
        assertEquals(uuid.hashCode(), orderId.hashCode());
    }

}