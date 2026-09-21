# Experimental Methodology

## Baseline

The baseline is a conventional blocking Java HTTP service. A request occupies a worker thread while the simulated downstream operation is in progress.

## Reactive treatment

The treatment is a Vert.x event-driven HTTP service. The simulated delay is scheduled asynchronously so the event-loop thread is not parked while waiting.

## Controlled workload

Both services expose the same logical operation:

```
HTTP request
   │
   ├── validate input
   ├── simulate downstream wait
   └── return response
```

Only the execution model changes.

## Variables

- Independent variable: execution model (blocking vs non-blocking).
- Load variables: concurrent users, request rate, test duration.
- Primary response variables: throughput and p99 latency.
- Secondary variables: CPU, memory, thread count, context switching.

## Reproducibility requirements

Record CPU/vCPU, RAM, OS, JVM, Maven, service versions, load-generator version, container limits, warm-up, duration, concurrency profile, and exact git commit.
