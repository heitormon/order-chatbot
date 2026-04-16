# Order Chatbot Project Flow

Este documento resume o fluxo principal do `order-service`, incluindo os endpoints HTTP, os casos de uso da aplicacao, o dominio, a persistencia em PostgreSQL e o fluxo opcional do assistente com LangChain4j/Ollama.

## Visao Geral

```mermaid
flowchart LR
    client["Client / API consumer"]
    postgres[("PostgreSQL")]
    ollama["Ollama / Local LLM"]

    subgraph entrypoint["Entrypoint"]
        direction TB
        orderController["OrderController"]
        exceptionHandler["ApiExceptionHandler"]
    end

    subgraph application["Application"]
        direction TB
        createUseCase["CreateOrderUseCase"]
        getUseCase["GetOrderUseCase"]
        askUseCase["AskOrderAssistantUseCase"]
    end

    subgraph outboundPorts["Outbound Ports"]
        direction TB
        orderRepositoryPort["OrderRepository"]
        transactionPort["TransactionExecutor"]
        dateTimePort["DateTimeProvider"]
        aiPort["AiAssistant"]
    end

    subgraph infrastructure["Infrastructure"]
        direction TB
        orderRepositoryImpl["OrderRepositoryImpl"]
        jpaOrderRepository["JpaOrderRepository"]
        transactionExecutor["TransactionExecutorImpl"]
        dateTimeProvider["DateTimeProviderImpl"]
        aiAssistantImpl["AiAssistantImpl"]
        aiClient["AiAssistantClient"]
        aiTools["AiTools"]
        aiConfig["AiConfig"]
    end

    subgraph domain["Domain"]
        direction TB
        orderId["OrderId"]
        order["Order"]
        orderStatus["OrderStatus"]
    end

    client --> orderController
    orderController --> createUseCase
    orderController --> getUseCase
    orderController --> askUseCase
    orderController --> exceptionHandler

    createUseCase --> orderRepositoryPort
    getUseCase --> orderRepositoryPort
    askUseCase --> aiPort
    createUseCase --> transactionPort
    createUseCase --> dateTimePort

    orderRepositoryPort --> orderRepositoryImpl
    orderRepositoryImpl --> jpaOrderRepository
    jpaOrderRepository --> postgres

    transactionPort --> transactionExecutor
    dateTimePort --> dateTimeProvider

    aiPort --> aiAssistantImpl
    aiAssistantImpl --> aiClient
    aiConfig --> aiClient
    aiClient --> ollama
    aiClient --> aiTools
    aiTools --> getUseCase

    createUseCase -. uses .-> orderId
    createUseCase -. creates .-> order
    getUseCase -. reads .-> order
    getUseCase -. maps .-> orderStatus
    getUseCase -. validates .-> orderId
```

## Fluxo de Criacao de Pedido

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Controller as OrderController
    participant UseCase as CreateOrderService
    participant Tx as TransactionExecutor
    participant Repo as OrderRepository
    participant Clock as DateTimeProvider
    participant Domain as Order / OrderId
    participant DB as PostgreSQL
    participant Handler as ApiExceptionHandler

    Client->>Controller: POST /orders { id }
    Controller->>UseCase: execute(Input id)
    UseCase->>Tx: execute(action)
    Tx->>UseCase: run action inside transaction
    UseCase->>Domain: OrderId.of(id)
    UseCase->>Repo: findById(orderId)
    Repo->>DB: select order
    DB-->>Repo: empty or existing order

    alt order already exists
        UseCase-->>Handler: ConflictException
        Handler-->>Client: 409 Conflict
    else new order
        UseCase->>Clock: now()
        Clock-->>UseCase: Instant
        UseCase->>Domain: Order.create(orderId, now)
        UseCase->>Repo: save(order)
        Repo->>DB: insert order
        Controller-->>Client: 201 Created
    end
```

## Fluxo de Consulta de Pedido

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Controller as OrderController
    participant UseCase as GetOrderUseCaseService
    participant Repo as OrderRepository
    participant Domain as OrderId
    participant DB as PostgreSQL
    participant Handler as ApiExceptionHandler

    Client->>Controller: GET /orders/{id}
    Controller->>UseCase: execute(Input id)
    UseCase->>Domain: OrderId.of(id)
    UseCase->>Repo: findById(orderId)
    Repo->>DB: select order
    DB-->>Repo: order or empty

    alt order found
        UseCase-->>Controller: Output(id, status, createdAt, updatedAt)
        Controller-->>Client: 200 OK GetOrderResponse
    else invalid id
        UseCase-->>Handler: InvalidInputException
        Handler-->>Client: 400 Bad Request
    else order not found
        UseCase-->>Handler: NotFoundException
        Handler-->>Client: 404 Not Found
    end
```

## Fluxo do Assistente com IA

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Controller as OrderController / Assistant endpoint
    participant AskUseCase as AskOrderAssistantService
    participant AiPort as AiAssistant
    participant AiImpl as AiAssistantImpl
    participant ClientLLM as AiAssistantClient
    participant Ollama as Ollama
    participant Tools as AiTools
    participant GetOrder as GetOrderUseCaseService
    participant Repo as OrderRepository
    participant DB as PostgreSQL

    Client->>Controller: pergunta em linguagem natural
    Controller->>AskUseCase: execute(message)
    AskUseCase->>AiPort: ask(message)
    AiPort->>AiImpl: ask(message)
    AiImpl->>ClientLLM: chat(message)
    ClientLLM->>Ollama: request to local model

    alt model asks for order details tool
        ClientLLM->>Tools: getOrderById(orderId)
        Tools->>GetOrder: execute(Input orderId)
        GetOrder->>Repo: findById(orderId)
        Repo->>DB: select order
        DB-->>Repo: order
        Repo-->>GetOrder: order
        GetOrder-->>Tools: Output
        Tools-->>ClientLLM: formatted order data
        ClientLLM->>Ollama: generate final answer with real data
    end

    Ollama-->>ClientLLM: response
    ClientLLM-->>AiImpl: answer
    AiImpl-->>AskUseCase: answer
    AskUseCase-->>Controller: response
    Controller-->>Client: answer
```

## Observacoes

- A camada `domain` nao depende de Spring, banco ou IA.
- Os casos de uso ficam na camada `application` e dependem de portas, nao de implementacoes concretas.
- A persistencia fica atras da porta `OrderRepository`.
- A integracao com IA deve ficar isolada na infraestrutura e idealmente condicionada por `app.ai.enabled=true`, para nao quebrar testes de integracao.
- O assistente deve usar casos de uso existentes, como `GetOrderUseCase`, em vez de acessar o banco diretamente.
