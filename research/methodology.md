# Experimental Methodology

## Research question

For an equivalent database-backed HTTP workload, how does a blocking Spring/Tomcat execution model compare with an event-driven Vert.x execution model as concurrency increases?

## Baseline

The baseline is a conventional Spring Boot HTTP service using JDBC/HikariCP. A request occupies a servlet worker thread while the JDBC operation waits for PostgreSQL. Spring Boot's JDBC starter uses HikariCP when available. ([Spring documentation](https://docs.spring.io/spring-boot/reference/data/sql.html)).

## Reactive treatment

The treatment is a Vert.x HTTP service using the Vert.x Reactive PostgreSQL Client. The database request is submitted asynchronously; the event-loop thread is not parked while PostgreSQL is waiting. Vert.x documents the client as reactive and non-blocking, with connection pooling. ([Vert.x documentation](https://vertx.io/docs/vertx-pg-client/java/)).

## Controlled workload

Both services expose the same logical operation:

~~~text
HTTP request
   │
   ├── parse delayMs + itemId
   ├── SELECT fixture row + pg_sleep(delayMs)
   └── return 200 response
~~~

The database, SQL semantics, request parameters, pool size and load profile are shared. The execution model, HTTP framework and client implementation change together. Valid-input behavior is the controlled scope; invalid-input and missing-row behavior are not equivalent.

## Variables

- Treatment: complete implementation stack (HTTP framework, execution model and database client). It is not an isolated execution-model intervention.
- Load variables: concurrent users, request rate, test duration.
- Primary response variables: throughput and p99 latency.
- Secondary variables: CPU, memory, thread count, context switching, error rate.
- Controlled variables: PostgreSQL fixture, delay, pool size, Java version, container limits, workload script.

## Threats to validity

- Docker Desktop and shared CI hosts introduce scheduling noise.
- PostgreSQL itself can become the bottleneck at high concurrency.
- A single fixture row is intentionally simple and does not represent a full enterprise schema.
- pg_sleep models waiting time, not CPU-heavy database work.
- The repository implementation is an experimental reference, not a claim that every legacy system will obtain the same improvement.

## Reproducibility requirements

Record the exact git commit, host resources, software versions, pool sizes, delay, warm-up, measurement duration, concurrency profile, raw k6 summary and derived analysis. Repeat runs and report variation rather than relying on one measurement.

## Required extensions before an empirical submission

Use the expanded procedure in `docs/reproducibility.md`. Run one implementation at a time; define warm-up separately; use fixed-load plateaus rather than a single ramp-wide percentile; report repeated runs and uncertainty. Capture successful throughput, semantic failures, timeouts, dropped iterations and loader saturation. Add representative CPU-bound and database-bound workloads. A virtual-thread baseline and controlled intermediate configurations remain unimplemented extensions.

The default pool size is 20; database waiting can dominate both stacks. The reactive service deploys one verticle and does not implement event-bus decomposition. No current repository data supports a causal context-switching explanation or any performance advantage.
