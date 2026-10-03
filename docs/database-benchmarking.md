# Database choice and GitHub Actions diagnosis

## Actual CI failure

The first startup failure was a non-executable Vert.x JAR; this was fixed by executable JAR packaging. The subsequent run (37106846299) reached PostgreSQL's SCRAM authentication handshake and failed because `com.ongres.scram.common.stringprep.StringPreparation` was missing from the runtime. Spring/JDBC had successfully connected to the same PostgreSQL service.

Vert.x 4.x requires the optional `com.ongres.scram:client:2.1` dependency for SCRAM-SHA-256 authentication. It is now explicitly included in the shaded JAR. Password authentication remains enabled.

Source: [Vert.x 4.x PostgreSQL authentication](https://vertx.io/docs/4.4.9/vertx-pg-client/java/). The dependency is documented for the 4.x line; the repository pins 4.5.13. [GitHub documents PostgreSQL service containers](https://docs.github.com/en/actions/tutorials/use-containerized-services/create-postgresql-service-containers); PostgreSQL is supported on Linux runners.

## Memory-backed PostgreSQL option

Use `docker-compose.memory.yml` together with the base Compose file:

```bash
docker compose -f docker-compose.yml -f docker-compose.memory.yml up --build -d
```

The override mounts the PostgreSQL 16 data directory on a 256 MiB tmpfs filesystem. It preserves the engine, SQL fixture, clients, network path, database pooling and authentication. The engine still uses sockets and its own process; this is memory-backed storage, not an embedded in-process database. Use a separate clean Compose project for each storage condition and keep benchmark run IDs distinct.

Inspect the mount:

```bash
docker inspect --format '{{json .Mounts}}' "$(docker compose -f docker-compose.yml -f docker-compose.memory.yml ps -q postgres)"
```

Find `Type: tmpfs` for `/var/lib/postgresql/data`. CI asserts this and tests both the ordinary volume and tmpfs configurations, including actual application queries. The initialization fixture mount from the base file remains in place.

Docker tmpfs is available on Linux and is temporary. The data is removed when the container stops, its usage counts toward container memory, and host swap can affect whether pages stay in RAM. Record swap settings, tmpfs size, container memory limits and database configuration. The 256 MiB limit suits the small fixture; assess and increase it for larger datasets. Preserve the same database durability settings in comparisons.

Source: [Docker tmpfs documentation](https://docs.docker.com/engine/storage/tmpfs/).

## Why H2 is a different experiment

H2 offers embedded in-memory databases and a JDBC API. It can support separate SQL/unit tests, but changing the engine and client path changes the measured treatment. Using H2 with a JDBC adapter on the reactive side would measure worker-offloaded blocking database calls instead of the current asynchronous PostgreSQL client. H2 compatibility modes do not establish PostgreSQL performance or full behavioral equivalence.

Keep the present PostgreSQL comparison for database I/O evaluation. If a future test isolates only HTTP scheduling, use the same well-defined fixture and delay semantics on both implementations, name it separately, and report its limited scope. A storage change also cannot fix a missing runtime authentication library.

Source: [H2 features](https://h2database.com/html/features.html).

## Evidence discipline

The GitHub Actions matrix is integration/correctness validation. Shared-runner timings are not the paper's performance results. Publication measurements must use the documented repeated-run protocol and retain raw metrics, environment records and source revisions. Report disk-backed and tmpfs results separately; neither can authenticate the historical manuscript numbers.
