package dev.nobuo.order_service.domain;

import dev.nobuo.order_service.domain.exception.DomainValidationException;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private OrderId id;

    @Column(nullable = false, updatable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private Instant updateDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column
    private String cancellationReason;

    private Order(OrderId id, Instant creationDate, Instant updateDate, OrderStatus status, String cancellationReason) {
        this.id = id;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.status = status;
        this.cancellationReason = cancellationReason;
    }

    protected Order() {
    }

    public static Order create(OrderId id, Instant creationDate) {
        if (id == null) {
            throw new DomainValidationException("id must not be null");
        }
        if (creationDate == null) {
            throw new DomainValidationException("creationDate must not be null");
        }
        return new Order(id, creationDate, creationDate, OrderStatus.CREATED, null);
    }

    public void confirm(Instant updateDate) {
        if (this.status != OrderStatus.CREATED) {
            throw new DomainValidationException("Cannot confirm an order that is not in CREATED status");
        }
        updateStatus(OrderStatus.CONFIRMED, updateDate);
    }

    public void cancel(Instant updateDate, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new DomainValidationException("cancellationReason must not be null or blank");
        }
        if (this.status != OrderStatus.CONFIRMED) {
            throw new DomainValidationException("Cannot cancel an order that is not in CONFIRMED status");
        }
        updateStatus(OrderStatus.CANCELLED, updateDate);
        this.cancellationReason = reason;
    }

    public void ship(Instant updateDate) {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new DomainValidationException("Cannot ship an order that is not in CONFIRMED status");
        }
        updateStatus(OrderStatus.SHIPPED, updateDate);
    }

    public void receive(Instant updateDate) {
        if (this.status != OrderStatus.SHIPPED) {
            throw new DomainValidationException("Cannot receive an order that is not in SHIPPED status");
        }

        updateStatus(OrderStatus.RECEIVED, updateDate);
    }

    private void updateStatus(OrderStatus newStatus, Instant updateDate) {
        if (updateDate == null) {
            throw new DomainValidationException("updateDate must not be null");
        }
        if (this.creationDate.isAfter(updateDate)) {
            throw new DomainValidationException("updateDate must be greater than or equal to creationDate");
        }
        if (this.updateDate.isAfter(updateDate)) {
            throw new DomainValidationException("updateDate must be greater than or equal to current updateDate");
        }
        this.updateDate = updateDate;
        this.status = newStatus;
    }

    public OrderId getId() {
        return id;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }
}
