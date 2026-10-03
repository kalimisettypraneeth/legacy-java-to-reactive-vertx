# Historical manuscript figures — unverified

These values came from the earlier manuscript. There are no accompanying raw runs in this repository. They are **not validated measurements, a benchmark baseline, or evidence for the revised article**. Recalculation fixes arithmetic only; it does not authenticate the inputs.

| Metric | Historical legacy | Historical Vert.x | Original delta | Recalculated change |
|---|---:|---:|---:|---:|
| Maximum throughput | 850 rps | 2,040 rps | +240% | +140.00% (2.40x baseline) |
| p99 latency | 1,850 ms | 295 ms | -84% | -84.05% |
| Context switches/s | 45,000 | 4,200 | -91% | -90.67% |
| Memory footprint | 3.2 GB | 1.1 GB | -65% | -65.63% |
| Estimated monthly OpEx | $450 | $162 | -64% | -64.00% |

Calculation: `(treatment - baseline) / baseline * 100`. The original manuscript additionally claimed 85% lower thread overhead without a supporting metric table. Its latency narrative referred to different concurrency points; the arithmetic above does not establish a matched-load comparison.

The earlier manuscript described JMeter on AWS t3.medium instances and 10-minute intervals with up to 5,000 users. This repository instead supplies a four-minute k6 ramp. Those are different protocols, and a future k6 run cannot retroactively authenticate the earlier JMeter claims.

Keep future raw runs separate and report their actual values, including negative or null outcomes. The active manuscript omits all of these numerical performance claims.
