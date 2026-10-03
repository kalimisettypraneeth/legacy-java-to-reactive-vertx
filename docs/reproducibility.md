# Reproducibility Protocol

Use an experiment ID such as EXP-2026-001.

Record:

~~~text
experiment_id=
git_commit=
date_utc=
host_cpu=
host_vcpu=
host_ram_gb=
os=
java_version=
maven_version=
docker_version=
k6_version=
postgres_version=
legacy_pool_size=
reactive_pool_size=
container_cpu_limit=
container_memory_limit=
warmup_seconds=
measurement_seconds=
delay_ms=
concurrency_profile=
~~~

## System under test

The two applications execute the same database-backed logical operation against the same PostgreSQL fixture:

~~~text
k6
 │
 ├── :8080 Spring/Tomcat ── JDBC/HikariCP ── PostgreSQL
 │                              │
 │                           pg_sleep
 │
 └── :8081 Vert.x ───────── Reactive PG ─── PostgreSQL
                                │
                             pg_sleep
~~~

The key treatment difference is the request/database execution model. Spring JDBC is blocking; Vert.x's reactive PostgreSQL client is non-blocking and event-driven. ([Spring JDBC configuration](https://docs.spring.io/spring-boot/reference/data/sql.html); [Vert.x PostgreSQL client](https://vertx.io/docs/vertx-pg-client/java/)).

## Procedure

1. Pin the exact git commit.
2. Start PostgreSQL and one service under test.
3. Confirm /health.
4. Warm up until latency stabilizes.
5. Execute the identical k6 scenario.
6. Export the k6 summary JSON.
7. Capture CPU, memory, thread and context-switch observations.
8. Repeat enough times to quantify run-to-run variation.
9. Save raw data before computing aggregates.
10. Repeat the same process for the other implementation.
11. Compare only after verifying environment and workload equivalence.

## Result classification

- **paper-reported** — copied from the manuscript for reference; not independently reproduced.
- **smoke** — correctness/build evidence.
- **exploratory** — benchmark run used to inspect behavior, not publication evidence.
- **reproduced** — independently generated under the documented protocol with raw data and environment metadata.

A publication result should not be labeled reproduced until the raw workload output, environment metadata, exact repository commit, and analysis artifact are retained together.

## Publication measurement requirements

- The default Compose file sets no application CPU/memory limits and uses a moving PostgreSQL 16 tag. Record resolved image digests and use documented equal limits in a dedicated measurement deployment.
- Capture JDK build, JVM flags, heap and stack settings, event-loop/worker configuration, database connection limits, query plan, fixture size, and loader hardware.
- Run one target at a time. Keep the competing application stopped. Preserve database initialization/cache policy between paired comparisons.
- Declare a separate warm-up phase and exclusion rule. Measure fixed load points in independent windows; do not present the supplied ramp-wide p99 as a fixed-load p99.
- Use repeated trials (plan at least five independent runs per condition, then assess variance), randomize or alternate run order, retain every run, and explain interval estimation. Do not average p99 values and label the result a pooled p99.
- Retain metrics streams, per-run summaries and checks, errors/timeouts, achieved and offered rates, and loader saturation. An open arrival model must also report dropped iterations.
- Record RSS, heap/GC, threads, context switches, application and database CPU, pool waiters, and queue depths with time alignment. OS-wide context switches cannot automatically be attributed to one service.
- For T3, record CPU-credit mode and balances/charges, or use non-burstable hardware. Record region, price date and purchase model.
- Validate a common service-level objective, such as an explicitly chosen p99 and error limit, before comparing required instance counts. Cost = application capacity + database + storage + networking + observability + applicable credit charges; list exclusions and engineering effort separately.
- Apply workloads that stress CPU, database capacity and dependency delay separately. The current `pg_sleep` fixture is not an enterprise production workload.

## Evidence gate

A future result table must map every row to run IDs, exact source revision, raw paths and analysis procedure. If original JMeter logs cannot be recovered, remove the earlier claims permanently and identify new runs as a new experiment. Never tune runs to recreate historical numbers. No benchmark was executed during the October 3 manuscript revision.
