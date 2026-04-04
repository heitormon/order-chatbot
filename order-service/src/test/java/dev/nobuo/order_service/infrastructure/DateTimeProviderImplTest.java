package dev.nobuo.order_service.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = DateTimeProviderImpl.class)
class DateTimeProviderImplTest {
    @Autowired
    DateTimeProviderImpl dateTimeProviderImpl;

    @Test
    void getNow() {
        assertNotNull(dateTimeProviderImpl);
        assertNotNull(dateTimeProviderImpl.now());
    }
}