package dev.nobuo.order_service.infrastructure;

import dev.nobuo.order_service.application.port.outbound.DateTimeProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DateTimeProviderImpl implements DateTimeProvider {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
