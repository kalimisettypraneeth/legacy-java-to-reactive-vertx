#!/usr/bin/env python3
"""Validate and export supported k6 aggregate summaries; never invent metrics."""
import argparse
import csv
import json
import math
from pathlib import Path

FIELDS = [
    ('http_req_duration', 'avg', 'avg_ms'),
    ('http_req_duration', 'med', 'p50_ms'),
    ('http_req_duration', 'p(90)', 'p90_ms'),
    ('http_req_duration', 'p(95)', 'p95_ms'),
    ('http_req_duration', 'p(99)', 'p99_ms'),
    ('http_req_duration', 'max', 'max_ms'),
    ('http_reqs', 'count', 'count'),
    ('http_reqs', 'rate', 'rate_per_s'),
    ('http_req_failed', 'rate', 'failure_rate'),
]

def extract_rows(data):
    metrics = data.get('metrics')
    if not isinstance(metrics, dict):
        raise ValueError('Missing metrics object; unsupported summary schema')
    rows = []
    for name, key, label in FIELDS:
        metric = metrics.get(name)
        if not isinstance(metric, dict):
            raise ValueError(f'Missing metric: {name}')
        values = metric.get('values', metric)  # handleSummary or legacy --summary-export
        if not isinstance(values, dict):
            raise ValueError(f'Invalid values for {name}')
        value = values.get(key)
        if isinstance(value, bool) or not isinstance(value, (int, float)) or not math.isfinite(value) or value < 0:
            raise ValueError(f'Missing/invalid {name}.{key}; include p(99) in summaryTrendStats')
        if name == 'http_req_failed' and value > 1:
            raise ValueError('HTTP failure rate must be in [0, 1]')
        if name == 'http_reqs' and key == 'count' and (value <= 0 or int(value) != value):
            raise ValueError('Request count must be a positive integer')
        rows.append((name, label, value))
    return rows

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('summary', type=Path)
    args = parser.parse_args()
    try:
        rows = extract_rows(json.loads(args.summary.read_text()))
    except (OSError, ValueError, TypeError, AttributeError) as exc:
        parser.exit(2, f'Invalid summary: {exc}\n')
    out = args.summary.with_name(args.summary.stem + '-analysis.csv')
    with out.open('w', newline='') as stream:
        writer = csv.writer(stream)
        writer.writerow(['metric', 'statistic', 'value'])
        writer.writerows(rows)
    print(out)

if __name__ == '__main__':
    main()
