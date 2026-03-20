# Order Chatbot

Este projeto é um sistema de pedidos desenvolvido para estudo, com foco em arquitetura orientada a múltiplas interfaces de entrada (`inbound`).

A aplicação possui dois tipos principais de interação:

- `REST`: responsável pelas operações do ciclo de vida do pedido:
    - criar pedido
    - confirmar pedido
    - cancelar pedido
    - enviar pedido
    - receber pedido
- `LangChain`: funciona como um chatbot para consultar e acompanhar o status dos pedidos em linguagem natural.

A proposta do projeto é demonstrar como um mesmo domínio de negócio pode ser exposto por diferentes canais, combinando uma API tradicional com uma experiência conversacional apoiada por IA. Com isso, o sistema serve como base de estudo para conceitos como modelagem de pedidos, arquitetura de software, integração entre serviços e uso de `LangChain` em cenários práticos.