package dev.nobuo.order_service.application.port.outbound;

import java.time.Instant;

public interface DateTimeProvider {
    Instant now();
}
