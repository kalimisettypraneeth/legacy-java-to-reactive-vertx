#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
DELAY_MS="${DELAY_MS:-50}"
SUMMARY="${SUMMARY:-benchmark-results/k6-summary.json}"
RAW="${RAW:-${SUMMARY%.json}-metrics.json.gz}"
ENVIRONMENT="${SUMMARY%.json}-environment.txt"
for output in "$SUMMARY" "$RAW" "$ENVIRONMENT"; do
  if [[ -e "$output" ]]; then
    echo "Refusing to overwrite $output; choose a unique SUMMARY/RAW path" >&2
    exit 1
  fi
  mkdir -p "$(dirname "$output")"
done
command -v k6 >/dev/null || { echo "k6 is required" >&2; exit 1; }
{
  echo "classification=exploratory"
  echo "git_commit=$(git rev-parse HEAD)"
  echo "git_dirty=$(git status --porcelain | wc -l)"
  echo "date_utc=$(date -u +%Y-%m-%dT%H:%M:%SZ)"
  echo "target=$BASE_URL"
  echo "delay_ms=$DELAY_MS"
  echo "scenario=closed-model concurrency ramp; four minutes plus graceful completion"
  echo "k6_version=$(k6 version)"
  echo "loader_os=$(uname -srm)"
  echo "warmup=not performed by this script"
  echo "sut_environment=must be recorded separately using docs/reproducibility.md"
} > "$ENVIRONMENT"
k6 run --out "json=$RAW"   -e SUMMARY="$SUMMARY"   -e BASE_URL="$BASE_URL"   -e DELAY_MS="$DELAY_MS"   benchmark/k6-concurrency.js
python3 benchmark/analyze_summary.py "$SUMMARY"
