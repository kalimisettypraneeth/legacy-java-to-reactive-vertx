package research.legacy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkController {
    private final JdbcTemplate jdbc;

    public WorkController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/work")
    public String work(
            @RequestParam(defaultValue = "50") long delayMs,
            @RequestParam(defaultValue = "1") long itemId) {

        long boundedDelay = Math.min(Math.max(delayMs, 0), 5000);
        long boundedItemId = Math.max(itemId, 1);

        // Deliberately blocking: the request thread waits synchronously for PostgreSQL.
        jdbc.queryForObject(
                "SELECT id, payload FROM work_items " +
                "WHERE id = ? AND pg_sleep(? / 1000.0) IS NULL",
                (rs, rowNum) -> rs.getLong("id"),
                boundedItemId,
                boundedDelay);

        return "legacy-ok";
    }

    @GetMapping("/health")
    public String health() {
        jdbc.queryForObject("SELECT 1", Integer.class);
        return "UP";
    }
}
