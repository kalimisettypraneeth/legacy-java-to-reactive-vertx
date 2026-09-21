# Reproducibility Protocol

Use an experiment ID such as `EXP-2026-001`.

Record:

```text
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
service_version=
container_cpu_limit=
container_memory_limit=
warmup_seconds=
measurement_seconds=
delay_ms=
concurrency_profile=
```

## Procedure

1. Start only the service under test.
2. Warm up until latency stabilizes.
3. Execute the identical k6 scenario.
4. Capture raw k6 output.
5. Capture CPU, memory, thread and context-switch observations.
6. Repeat enough times to quantify run-to-run variation.
7. Save raw data before computing aggregates.
8. Compare implementations only after verifying environment and workload equivalence.

A benchmark result is incomplete without its workload and environment.
