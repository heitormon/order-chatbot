package dev.nobuo.order_service.application.port.outbound;

public interface TransactionExecutor {
    void execute(Runnable action);
}
