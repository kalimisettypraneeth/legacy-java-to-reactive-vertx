#!/usr/bin/env python3
"""Convert a k6 --summary-export JSON file into a compact CSV table."""
import csv
import json
import sys
from pathlib import Path

if len(sys.argv) != 2:
    raise SystemExit("usage: python benchmark/analyze_summary.py <summary.json>")

src = Path(sys.argv[1])
data = json.loads(src.read_text())
metrics = data.get("metrics", {})

def value(metric, key):
    return metrics.get(metric, {}).get("values", {}).get(key, "")

rows = [
    ("http_req_duration", "avg_ms", value("http_req_duration", "avg")),
    ("http_req_duration", "p50_ms", value("http_req_duration", "med")),
    ("http_req_duration", "p90_ms", value("http_req_duration", "p(90)")),
    ("http_req_duration", "p95_ms", value("http_req_duration", "p(95)")),
    ("http_req_duration", "p99_ms", value("http_req_duration", "p(99)")),
    ("http_req_duration", "max_ms", value("http_req_duration", "max")),
    ("http_reqs", "count", value("http_reqs", "count")),
    ("http_reqs", "rate_per_s", value("http_reqs", "rate")),
    ("http_req_failed", "failure_rate", value("http_req_failed", "rate")),
]

out = src.with_name(src.stem + "-analysis.csv")
with out.open("w", newline="") as f:
    writer = csv.writer(f)
    writer.writerow(["metric", "statistic", "value"])
    for metric, statistic, val in rows:
        writer.writerow([metric, statistic, val])

print(out)
