package dev.nobuo.order_service.infrastructure.ai;

import dev.nobuo.order_service.application.port.outbound.AiAssistant;
import org.springframework.stereotype.Component;

@Component
public class AiAssistantImpl implements AiAssistant {
    private final AiAssistantClient aiAssistantClient;

    public AiAssistantImpl(AiAssistantClient aiAssistantClient) {
        this.aiAssistantClient = aiAssistantClient;
    }

    @Override
    public String answer(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("userMessage cannot be null");
        }
        return aiAssistantClient.chat(userMessage);
    }
}
