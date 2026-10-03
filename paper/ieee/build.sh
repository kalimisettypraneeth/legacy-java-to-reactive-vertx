#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$root"
pandoc paper/ieee/manuscript.md --standalone \
  --pdf-engine=pdflatex -V geometry:margin=1in -V fontsize=11pt \
  -V colorlinks=true -o paper/ieee/manuscript.pdf
