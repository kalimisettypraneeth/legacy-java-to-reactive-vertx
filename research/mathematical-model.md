# Mathematical Model

## Little's Law

The manuscript uses Little's Law:

```
L = λW
```

where L is the average number of items in the system, λ is arrival rate, and W is average time in the system.

The manuscript models memory as:

```
M_total = (λW) × M_s + M_heap
```

where M_s represents per-thread stack memory.

The reactive model instead keeps a comparatively small event-loop thread set and represents waiting work as lightweight asynchronous state.

## Interpretation

The equations describe why increased concurrency can produce different resource behavior even when the business operation is unchanged. They should be treated as a model of the system, not a substitute for direct measurement.
