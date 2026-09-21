package research.legacy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkController {
    @GetMapping("/work")
    public String work(@RequestParam(defaultValue = "50") long delayMs) throws InterruptedException {
        long boundedDelay = Math.min(Math.max(delayMs, 0), 5000);
        // Deliberately blocking: models a synchronous downstream dependency.
        Thread.sleep(boundedDelay);
        return "legacy-ok";
    }

    @GetMapping("/health")
    public String health() { return "UP"; }
}