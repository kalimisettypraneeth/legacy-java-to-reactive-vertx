# Benchmarking

Run the exact same test against both services:

```bash
BASE_URL=http://localhost:8080 k6 run benchmark/k6-concurrency.js
BASE_URL=http://localhost:8081 k6 run benchmark/k6-concurrency.js
```

The default workload ramps from 50 to 5,000 virtual users and simulates a 50 ms downstream wait.

Capture requests/sec, p50/p95/p99 latency, error rate, CPU, memory, thread count, and context switches.

Docker observation:

```bash
docker stats
```

Linux observation:

```bash
pidstat -w -p <PID> 1
pidstat -r -u -p <PID> 1
```

The manuscript's published numbers are not automatically reproduced by this reference implementation. The workload and environment must be calibrated before comparing values.
