package dev.nobuo.order_service.infrastructure.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    AiAssistantClient orderAssistant(ChatModel chatModel, AiTools orderTools) {
        return AiServices.builder(AiAssistantClient.class)
                .chatModel(chatModel)
                .tools(orderTools)
                .build();
    }

    @Bean
    ChatModel chatModel(@Value("${app.ai.ollama.base-url}") String baseUrl,
                        @Value("${app.ai.ollama.model}") String model,
                        @Value("${app.ai.ollama.temperature}") Double temperature) {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(model)
                .temperature(temperature)
                .build();
    }
}
