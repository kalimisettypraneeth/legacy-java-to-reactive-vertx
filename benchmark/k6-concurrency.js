import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
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
