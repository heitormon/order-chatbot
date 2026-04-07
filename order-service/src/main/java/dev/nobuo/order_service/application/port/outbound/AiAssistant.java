package dev.nobuo.order_service.application.port.outbound;

public interface AiAssistant {
    String answer(String userMessage);
}
