package dev.nobuo.order_service.infrastructure;

import dev.nobuo.order_service.application.port.outbound.TransactionExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(TransactionExecutorImplTest.TestConfig.class)
class TransactionExecutorImplTest {

    @Autowired
    TransactionExecutor transactionExecutor;

    @Test
    void execute() {
        transactionExecutor.execute(() -> assertTrue(TransactionSynchronizationManager.isActualTransactionActive()));
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Configuration
    @EnableTransactionManagement
    static class TestConfig {

        @Bean
        TransactionExecutorImpl transactionExecutor() {
            return new TransactionExecutorImpl();
        }

        @Bean
        PlatformTransactionManager platformTransactionManager() {
            return new StubPlatformTransactionManager();
        }
    }

    static class StubPlatformTransactionManager extends AbstractPlatformTransactionManager {

        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
            // No resource to bind: Spring transaction synchronization is enough for this test.
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
        }
    }
}
