package dev.nobuo.order_service.domain;

import dev.nobuo.order_service.domain.exception.DomainValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class OrderId {

    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    private OrderId(String id) {
        this.id = id;
    }

    protected OrderId() {
    }

    public static OrderId of(String id) {
        if (id == null) {
            throw new DomainValidationException("id must not be null");
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

    @Override
    public String toString() {
        return id;
    }
}
