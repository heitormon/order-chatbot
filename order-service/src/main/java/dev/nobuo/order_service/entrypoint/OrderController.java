package dev.nobuo.order_service.entrypoint;

import dev.nobuo.order_service.application.port.inbound.CreateOrderUseCase;
import dev.nobuo.order_service.application.port.inbound.GetOrderUseCase;
import dev.nobuo.order_service.entrypoint.dto.CreateOrderRequest;
import dev.nobuo.order_service.entrypoint.dto.GetOrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, GetOrderUseCase getOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
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
}