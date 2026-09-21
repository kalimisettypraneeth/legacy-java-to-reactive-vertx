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

The key treatment difference is the request/database execution model. Spring JDBC is blocking; Vert.x's reactive PostgreSQL client is non-blocking and event-driven. citeturn0search5turn0search0

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
