package dev.nobuo.order_service.entrypoint;

import dev.nobuo.order_service.application.port.inbound.AskOrderAssistantUseCase;
import dev.nobuo.order_service.application.port.inbound.CreateOrderUseCase;
import dev.nobuo.order_service.application.port.inbound.GetOrderUseCase;
import dev.nobuo.order_service.entrypoint.dto.AskRequest;
import dev.nobuo.order_service.entrypoint.dto.AskResponse;
import dev.nobuo.order_service.entrypoint.dto.CreateOrderRequest;
import dev.nobuo.order_service.entrypoint.dto.GetOrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final AskOrderAssistantUseCase askOrderAssistantUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           GetOrderUseCase getOrderUseCase,
                           AskOrderAssistantUseCase askOrderAssistantUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.askOrderAssistantUseCase = askOrderAssistantUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody CreateOrderRequest request) {
        createOrderUseCase.execute(new CreateOrderUseCase.Input(request.id()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GetOrderResponse get(@PathVariable String id) {
        GetOrderUseCase.Output output = getOrderUseCase.execute(new GetOrderUseCase.Input(id));
        return new GetOrderResponse(output.id(), output.status(), output.createdAt(), output.updatedAt());
    }

    @PostMapping("/ask")
    @ResponseStatus(HttpStatus.CREATED)
    public AskResponse ask(@RequestBody AskRequest request) {
        AskOrderAssistantUseCase.Output output = askOrderAssistantUseCase.execute(new AskOrderAssistantUseCase.Input(request.message(), request.messageId()));
        return new AskResponse(output.response());
    }
}