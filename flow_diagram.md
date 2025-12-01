# Programmatic Flow Diagrams

## 1. High-Level Component Architecture

This diagram illustrates how the different components of the **Reactive Universal Question Answer Platform** interact with each other, the database, and the message broker.

```mermaid
graph TD
    Client[Client Application]
    
    subgraph "Spring WebFlux Application"
        Controller[QuestionController]
        Service[QuestionService]
        Repo[IQuestionRepository]
        Producer[KafkaEventProducer]
        Consumer[KafkaEventConsumer]
    end
    
    subgraph "Infrastructure"
        MongoDB[(MongoDB)]
        Kafka{Apache Kafka}
    end

    Client -->|HTTP GET/POST| Controller
    Controller -->|Calls| Service
    Service -->|Reactive CRUD| Repo
    Repo -->|R2DBC/Reactive Streams| MongoDB
    
    Service -->|Publish Event| Producer
    Producer -->|Send Message| Kafka
    
    Kafka -->|Consume Message| Consumer
    Consumer -->|Update View Count| Repo
```

## 2. Sequence Diagram: Get Question & Async View Count

This sequence diagram details the flow when a user fetches a question. It highlights the **non-blocking** nature of the main request and the **asynchronous** handling of the view count increment via Kafka.

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant QC as QuestionController
    participant QS as QuestionService
    participant QR as IQuestionRepository
    participant DB as MongoDB
    participant KP as KafkaEventProducer
    participant K as Kafka (Topic: view-counts)
    participant KC as KafkaEventConsumer

    Note over User, QC: Main Request Flow (Reactive)

    User->>QC: GET /api/questions/{id}
    QC->>QS: getQuestionById(id)
    QS->>QR: findById(id)
    QR->>DB: Query Document
    DB-->>QR: Return Question (Mono)
    QR-->>QS: Return Question
    
    par Parallel Execution
        QS->>QS: Map to ResponseDTO
        QS->>KP: publishViewCountEvent(id) (Async Side-effect)
    end
    
    KP->>K: Send ViewCountEvent
    
    QS-->>QC: Return Mono<QuestionResponseDto>
    QC-->>User: 200 OK (JSON Response)

    Note over K, KC: Asynchronous Event Processing

    K->>KC: Consume ViewCountEvent
    KC->>QR: findById(targetId)
    QR->>DB: Query Question
    DB-->>QR: Return Question
    KC->>KC: Increment view count
    KC->>QR: save(question)
    QR->>DB: Update Document
    DB-->>QR: Return Updated Question
    QR-->>KC: Acknowledge
```

## 3. Key Data Flows

### A. Question Creation (Write)
1.  **Client** sends `POST /api/questions` with JSON payload.
2.  **Controller** delegates to `QuestionService.createQuestion`.
3.  **Service** converts DTO to Entity and sets timestamps.
4.  **Repository** saves to **MongoDB**.
5.  **Service** returns the created object to **Client**.

### B. Infinite Scroll (Read)
1.  **Client** sends `GET /api/questions?cursor={timestamp}`.
2.  **Controller** calls `QuestionService.getAllQuestions`.
3.  **Service** validates cursor.
    *   If valid: Queries MongoDB for records `createdAt > cursor`.
    *   If null: Queries top 10 records ordered by `createdAt`.
4.  **Repository** executes optimized range query.
5.  **Service** returns `Flux<QuestionResponseDto>` to **Client**.
