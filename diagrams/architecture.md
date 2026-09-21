# Architecture Diagrams

## End-to-end experiment

~~~mermaid
flowchart LR
    L[Load Generator / k6] --> B[Spring + Tomcat]
    L --> R[Vert.x HTTP]

    B --> J[JDBC / HikariCP]
    J --> P[(PostgreSQL)]
    P --> S1[pg_sleep / DB wait]
    S1 --> J

    R --> V[Reactive PostgreSQL Client]
    V --> P
    P --> S2[pg_sleep / DB wait]
    S2 --> V

    J --> B
    V --> R
~~~

## Execution-model difference

~~~mermaid
sequenceDiagram
    participant C as Client
    participant B as Blocking Service
    participant R as Reactive Service
    participant DB as PostgreSQL

    C->>B: HTTP /work
    B->>DB: JDBC query
    Note over B: Worker thread waits
    DB-->>B: Row
    B-->>C: 200

    C->>R: HTTP /work
    R->>DB: Async query
    Note over R: Event loop remains available
    DB-->>R: Row / callback
    R-->>C: 200
~~~

The controlled experiment changes the database client and execution model while keeping the logical operation, PostgreSQL fixture and requested delay equivalent.
