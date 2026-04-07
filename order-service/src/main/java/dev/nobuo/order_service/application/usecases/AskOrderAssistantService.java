package dev.nobuo.order_service.application.usecases;

import dev.nobuo.order_service.application.exception.InvalidInputException;
import dev.nobuo.order_service.application.port.inbound.AskOrderAssistantUseCase;
import dev.nobuo.order_service.application.port.outbound.AiAssistant;
import org.springframework.stereotype.Service;

@Service
public class AskOrderAssistantService implements AskOrderAssistantUseCase {
    private final AiAssistant aiAssistant;

    public AskOrderAssistantService(AiAssistant aiAssistant) {
        this.aiAssistant = aiAssistant;
    }

    @Override
    public Output execute(Input input) {
        if (input == null) {
            throw new InvalidInputException("input must not be null");
        }
        if (isNullOrBlank(input.message())){
            throw new InvalidInputException("message must not be null or blank");
        }
        if(isNullOrBlank(input.messageId())){
            throw new InvalidInputException("messageId must not be null or blank");
        }
        String answer = aiAssistant.answer(input.message());

        return new Output(answer);
    }

    private boolean isNullOrBlank(String message) {
        if (message == null) {
            return true;
        }
        return message.trim().isBlank();
    }
}
