import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  summaryTrendStats: ["avg", "min", "med", "max", "p(90)", "p(95)", "p(99)"],
  scenarios: {
    concurrency_ramp: {
      executor: "ramping-vus",
      startVUs: 50,
      stages: [
        { duration: "30s", target: 250 },
        { duration: "60s", target: 1000 },
        { duration: "60s", target: 2000 },
        { duration: "60s", target: 5000 },
        { duration: "30s", target: 0 }
      ],
      gracefulRampDown: "10s"
    }
  }
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const DELAY_MS = __ENV.DELAY_MS || "50";

export default function () {
  const response = http.get(`${BASE_URL}/work?delayMs=${DELAY_MS}`);
  check(response, { "HTTP 200": (r) => r.status === 200 });
  sleep(0.01);
}

// Nested metric values consumed by analyze_summary.py. A summary is aggregate data.
export function handleSummary(data) {
  return {
    [__ENV.SUMMARY || "benchmark-results/k6-summary.json"]: JSON.stringify(data, null, 2),
    stdout: "Exploratory ramp complete. Aggregate summary saved; inspect errors and checks.\n"
  };
}
