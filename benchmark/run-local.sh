#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
DELAY_MS="${DELAY_MS:-50}"
SUMMARY="${SUMMARY:-benchmark-results/k6-summary.json}"

mkdir -p "$(dirname "$SUMMARY")"

k6 run \
  --summary-export "$SUMMARY" \
  -e BASE_URL="$BASE_URL" \
  -e DELAY_MS="$DELAY_MS" \
  benchmark/k6-concurrency.js
