package dev.nobuo.order_service.domain;

import dev.nobuo.order_service.domain.exception.DomainValidationException;

import java.util.UUID;

import static dev.nobuo.order_service.utils.GuardUtils.isNullOrEmpty;

public class OrderId {
    private final String id;

    private OrderId(String id) {
        this.id = id;
    }

    public static OrderId of(String id) {
        if (isNullOrEmpty(id)) {
            throw new DomainValidationException("id must not be null or empty");
        }
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new DomainValidationException("id must be a valid UUID");
        }
        return new OrderId(id);
    }

    public String getValue() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId that = (OrderId) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
