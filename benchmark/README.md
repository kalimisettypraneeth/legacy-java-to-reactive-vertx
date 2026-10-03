# Benchmarking

## Goal

Measure the same logical workload against:

- Spring/Tomcat blocking implementation on :8080
- Vert.x event-driven implementation on :8081
- PostgreSQL 16 fixture shared by both implementations

The application-level delay is implemented as PostgreSQL pg_sleep, so the experiment exercises a real database wait rather than only an in-process timer. The legacy path waits synchronously on JDBC; the Vert.x path submits an asynchronous PostgreSQL operation and releases the event loop while the database is waiting. Spring Boot's JDBC starter uses HikariCP by default, while Vert.x provides a reactive PostgreSQL client designed for non-blocking database access. ([Spring JDBC configuration](https://docs.spring.io/spring-boot/reference/data/sql.html); [Vert.x PostgreSQL client](https://vertx.io/docs/vertx-pg-client/java/)).

## Start the stack

~~~
docker compose up --build
~~~

Check:

~~~
curl http://localhost:8080/health
curl http://localhost:8081/health
~~~

Expected response: UP.

## Run one measured target

~~~
BASE_URL=http://localhost:8080 DELAY_MS=50   bash benchmark/run-local.sh

BASE_URL=http://localhost:8081 DELAY_MS=50   SUMMARY=benchmark-results/reactive-k6-summary.json   bash benchmark/run-local.sh
~~~

The default k6 workload ramps from 50 to 5,000 VUs. For quick smoke tests, use the k6 script directly with a shorter scenario or a small VU count.

## Analyze a summary

~~~
python3 benchmark/analyze_summary.py benchmark-results/reactive-k6-summary.json
~~~

This validates a nested handleSummary or legacy flat summary and creates a *-analysis.csv next to it. Missing p99 and invalid metric values cause a nonzero exit; unsupported schemas are not silently interpreted. The k6 script explicitly requests p99.

## Publication discipline

Never copy the manuscript's historical values into a new-results table.

Every reproducible run should record:

1. git commit SHA
2. Java, Maven, Docker and k6 versions
3. CPU, memory and OS
4. PostgreSQL version and pool size
5. request delay and workload stages
6. warm-up policy
7. raw k6 summary
8. generated analysis CSV
9. date/time and target implementation

CI smoke tests are for correctness, not publication-grade performance measurements. Cloud-hosted CI introduces shared-resource noise; final IEEE evidence should use a controlled benchmark environment and repeated runs.

## Important limits of the supplied runner

The four-minute ramp is exploratory and closed-model. Its aggregate p99 spans changing concurrency; it is not p99 at 5,000 VUs and is not a maximum sustainable capacity estimate. No warm-up exclusion or repeated steady-state experiment is implemented by this runner. Use separate warm-up and fixed-load measurement runs for publication evidence.

`run-local.sh` writes a nested JSON aggregate through `handleSummary`, a compressed JSON metrics stream, and partial environment metadata. It refuses to overwrite existing output. Set a unique `SUMMARY` per run; `RAW` optionally overrides the metrics-stream path. Complete the SUT environment record separately, and inspect HTTP errors and semantic checks before interpreting throughput. The summary request rate includes failed requests.

Run analyzer regression checks with `python3 -m unittest discover -s benchmark -p 'test_*.py'`. Fixtures are synthetic and never publication data.

Sources: [k6 custom summary](https://grafana.com/docs/k6/latest/results-output/end-of-test/custom-summary/), [k6 results output](https://grafana.com/docs/k6/latest/get-started/results-output/).
