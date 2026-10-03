# Manuscript correction and evidence audit

Audit date: October 3, 2026. Repository inspected at `16f9ff694ecf7bd1c59a0231bd364cd6976ba62e`.
Original source: five-page author PDF titled “Performance and Cost Optimization in Enterprise Systems: A Quantitative Analysis of Migrating Legacy Java Monoliths to Reactive Vert.x Architectures.”

| Earlier claim or defect | Revision and reason |
|---|---|
| Throughput +240%, 850 to 2,040 rps | Correct arithmetic is +140%, or 2.4x baseline. Historical audit retains both; active manuscript omits unverified figures. |
| 85% thread-overhead reduction | Removed; no definition or source measurements available. |
| p99 comparison at differing concurrency points | Removed; require matched load, error budgets and measurement windows. |
| Context switching is the primary cause | Removed causal inference; requires profiling/controlled ablation. |
| Memory is nearly constant under reactive load | Replaced with request-state, buffer, native-memory and queue discussion. |
| Thread count equals lambda W | Corrected for bounded pools and queues; W is total in-system time. |
| Event-loop count is total thread count | Corrected; runtime and worker threads must be measured. |
| Verticle decomposition and event bus implemented | Removed; repository deploys one verticle and has no event-bus migration implementation. |
| $450 to $162 proves savings | Removed; cost inputs, deployment sizing, price date and SLO equivalence absent. |
| Reactive migration is a necessity | Replaced with conditional engineering decision and alternatives. |
| JMeter results reproduced by this repository | Explicitly disclaimed: repository has k6, a different duration and no raw runs. |
| Generic AI refactoring future-work section | Removed from current scope; no evaluated refactoring method. |
| Bibliography [7], S. Thompson database-driver article | Removed: not used or relied on; no verified bibliographic record supplied. |
| BERT used to support code refactoring | Removed: does not substantiate the stated implementation claim. |
| IEEE 1471-2000 called a modernization standard | Removed; actual title is Recommended Practice for Architectural Description for Software-Intensive Systems. |
| Broad books treated as empirical evidence | Replaced with directly relevant primary technical sources; no claim that these establish novelty. |
| Render-specific citation tokens in repo docs | Replaced with durable source URLs. |
| p99 omitted by default benchmark summary | Explicit summaryTrendStats added. |
| Analyzer silently emitted blank values for incompatible JSON | Validate nested and legacy flat schemas; fail on missing/invalid required statistics. |

## Validation limits

Arithmetic and analysis tooling are locally checkable. This environment has no Docker, Maven, or k6 executable, and direct GitHub cloning is unavailable. Repository files were read and committed through the authenticated GitHub connection. No application build, cloud experiment, or publication-grade benchmark was performed here.

The revised manuscript is a practitioner-method draft. Its readiness still depends on author review, a photograph, ORCID/exclusivity declarations, a related-work contribution check, and empirical evidence if submitted as an evaluation article. No numerical output in a unit-test fixture is a real benchmark result.

## Checks performed for this commit

- Five Python regression tests pass (nested/flat summaries, absent p99, invalid rates, unsupported schemas, zero requests).
- Bash syntax checks pass for benchmark runner and PDF build script.
- JavaScript module syntax check passes for the k6 workload.
- Pandoc/pdfLaTeX generates a five-page review PDF. All pages were rendered and inspected; no extracted text falls outside the checked horizontal margins.
- Abstract: 104 whitespace-delimited words. Pre-reference material: approximately 1,762 words, including metadata. Seven references. Below the venue's stated limits.

These checks do not establish application correctness or empirical performance. GitHub CI must still build and smoke-test the applications; results must be checked after the commit.

## CI follow-up: executable packaging

GitHub Actions run 37106540718 built both images but failed startup because the pre-existing Vert.x JAR lacked a Main-Class manifest entry (`no main manifest attribute, in app.jar`). The Maven package lifecycle now creates an executable dependency-inclusive JAR, merges service descriptors, and names the application entry point. The HTTP smoke check remains the validation gate. This packaging defect is distinct from benchmark evidence.
