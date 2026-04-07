package dev.nobuo.order_service.application.port.inbound;

public interface AskOrderAssistantUseCase {
    record Input(String message, String messageId) {}

    record Output(String response) {}

    Output execute(Input input);
}
