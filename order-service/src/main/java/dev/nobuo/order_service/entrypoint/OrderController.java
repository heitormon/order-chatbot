package dev.nobuo.order_service.entrypoint;

import dev.nobuo.order_service.application.port.inbound.CreateOrderUseCase;
import dev.nobuo.order_service.entrypoint.dto.CreateOrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody CreateOrderRequest request) {
        createOrderUseCase.execute(new CreateOrderUseCase.Input(request.id()));
    }
}