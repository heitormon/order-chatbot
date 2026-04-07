package dev.nobuo.order_service.entrypoint;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("truncate table orders");
    }

    @Test
    void createOrder() throws Exception {
        String orderId = UUID.randomUUID().toString();
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "%s"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/orders/" + orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id": "%s",
                            "status": "CREATED"
                        }
                        """.formatted(orderId)));
    }

    @Test
    void returnConflictWhenOrderAlreadyExists() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "55fa4ba8-2b1e-4525-be6e-9389efa8416b"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "55fa4ba8-2b1e-4525-be6e-9389efa8416b"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().json("""
                        {
                          "message": "order already exists"
                        }
                        """));
    }

    @Test
    void returnInvalidInput() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "invalid-id"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                          "message": "id must be a valid UUID"
                        }
                        """));
    }

    @Test
    void returnNotFound() throws Exception {
        String orderId = UUID.randomUUID().toString();

        mockMvc.perform(get("/orders/" + orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                          "message": "Order with id %s not found"
                        }
                        """.formatted(orderId)));
    }
}