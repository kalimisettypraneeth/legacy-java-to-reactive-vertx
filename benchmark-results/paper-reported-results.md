# Results Reported by the Manuscript

These values are preserved as a reference baseline. They are **not newly reproduced measurements**.

| Metric | Legacy Monolith | Vert.x Reactive | Reported delta |
|---|---:|---:|---:|
| Max Throughput | 850 rps | 2,040 rps | +240% |
| p99 Latency | 1,850 ms | 295 ms | -84% |
| Context Switches/s | 45,000 | 4,200 | -91% |
| Memory Footprint | 3.2 GB | 1.1 GB | -65% |
| Estimated OpEx/mo | $450 | $162 | -64% |

The manuscript states that the stress test used AWS t3.medium instances (2 vCPUs, 4 GB RAM) and JMeter, with up to 5,000 concurrent users over 10-minute intervals.

## Reproduction status

**Reference values only.** Before using these numbers as experimental evidence for an IEEE submission, run the repository implementation under a documented controlled environment and preserve the raw output. Differences must be reported rather than silently normalized.
