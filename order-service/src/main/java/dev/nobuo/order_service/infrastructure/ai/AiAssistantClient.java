package dev.nobuo.order_service.infrastructure.ai;

import dev.langchain4j.service.SystemMessage;

public interface AiAssistantClient {
    @SystemMessage("""
        You are an assistant for the order service.

        When the user asks about an order and there is an order ID in the message,
        you must call the available tool to fetch the real order data.
        Do not invent order details.
        """)
    String chat(String userMessage);
}
