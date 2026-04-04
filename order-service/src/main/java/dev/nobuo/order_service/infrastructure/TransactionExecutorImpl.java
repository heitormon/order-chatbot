package dev.nobuo.order_service.infrastructure;

import dev.nobuo.order_service.application.port.outbound.TransactionExecutor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class TransactionExecutorImpl implements TransactionExecutor {

    @Override
    @Transactional
    public void execute(Runnable action) {
        action.run();
    }
}
