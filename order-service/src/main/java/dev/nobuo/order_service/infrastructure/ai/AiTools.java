package dev.nobuo.order_service.infrastructure.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.nobuo.order_service.application.port.inbound.GetOrderUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AiTools {
    private static final Logger log = LoggerFactory.getLogger(AiTools.class);

    private final GetOrderUseCase getOrderUseCase;

    public AiTools(GetOrderUseCase getOrderUseCase) {
        this.getOrderUseCase = getOrderUseCase;
    }

    @Tool("Get an order by its id")
    public String getOrderById(String id) {
        log.info("Get an order by its id: {}", id);
        GetOrderUseCase.Output order = getOrderUseCase.execute(new GetOrderUseCase.Input(id));
        return """
                Order %s is in status %s. Created at %s and updated at %s.
                """.formatted(order.id(), order.status(), order.createdAt(), order.updatedAt());
    }
}
