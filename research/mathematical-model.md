# Queueing and memory model

For a defined, stable system, Little’s Law is `L = lambda * W`: L is mean in-system work, lambda is long-run throughput, and W is mean total time inside the same boundary (waiting plus service). Do not combine offered arrivals, completed throughput and latency across different boundaries or use this as a steady-state formula during unbounded overload.

A bounded servlet worker pool means L is not the platform-thread count: requests can wait in connection, executor, and database-pool queues. The earlier formula `M_total = lambda * W * stack_size + heap` is therefore not a general process-memory model.

Use a measured decomposition instead:

`RSS approximately equals resident heap + committed resident stacks + direct buffers + other native/runtime memory`.

Reserved stack address space is not identical to resident memory. Asynchronous requests also retain state and buffers. Event-loop thread count does not equal total JVM thread count, and a fixed loop pool does not guarantee constant memory.

With P connections, one active sequential query per connection, and mean occupied time S, `P / S` is an idealized database capacity bound before overhead. For P=20 and S=0.05 seconds, this is 400 queries/s. This is explanatory arithmetic, NOT a measured benchmark. Check actual pipelining and connection behavior before applying it.

Source: J. D. C. Little, “A Proof for the Queuing Formula: L = lambda W,” Operations Research 9(3), 383–387 (1961), https://doi.org/10.1287/opre.9.3.383.
