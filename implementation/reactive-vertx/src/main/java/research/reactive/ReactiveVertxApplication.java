package research.reactive;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;

public class ReactiveVertxApplication extends AbstractVerticle {
    private Pool db;

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new ReactiveVertxApplication());
    }

    @Override
    public void start(Promise<Void> startPromise) {
        String host = env("DB_HOST", "postgres");
        int port = Integer.parseInt(env("DB_PORT", "5432"));
        String database = env("DB_NAME", "research");
        String user = env("DB_USER", "research");
        String password = env("DB_PASSWORD", "research");
        int poolSize = Integer.parseInt(env("DB_POOL_SIZE", "20"));

        PgConnectOptions connect = new PgConnectOptions()
                .setHost(host)
                .setPort(port)
                .setDatabase(database)
                .setUser(user)
                .setPassword(password);

        db = PgBuilder.pool()
                .with(new PoolOptions().setMaxSize(poolSize))
                .connectingTo(connect)
                .using(vertx)
                .build();

        Router router = Router.router(vertx);
        router.get("/health").handler(ctx ->
                db.query("SELECT 1").execute()
                        .onSuccess(rows -> ctx.response().end("UP"))
                        .onFailure(err -> ctx.response().setStatusCode(503).end("DOWN")));

        router.get("/work").handler(ctx -> {
            long delayMs = parseDelay(ctx.request().getParam("delayMs"));
            long itemId = parseItemId(ctx.request().getParam("itemId"));

            // Non-blocking: the event loop submits the PostgreSQL operation and is released
            // while the database is waiting on pg_sleep().
            db.preparedQuery(
                    "SELECT id, payload FROM work_items " +
                    "WHERE id = $1 AND pg_sleep($2) IS NULL")
              .execute(Tuple.of(itemId, delayMs / 1000.0))
              .onSuccess(rows -> {
                  if (rows.rowCount() == 1) {
                      ctx.response().end("reactive-ok");
                  } else {
                      ctx.response().setStatusCode(404).end("not-found");
                  }
              })
              .onFailure(err -> ctx.response().setStatusCode(500).end("database-error"));
        });

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router).listen(8081)
              .onSuccess(ignored -> startPromise.complete())
              .onFailure(startPromise::fail);
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static long parseDelay(String raw) {
        try {
            return Math.min(Math.max(Long.parseLong(raw == null ? "50" : raw), 0), 5000);
        } catch (NumberFormatException ex) {
            return 50;
        }
    }

    private static long parseItemId(String raw) {
        try {
            return Math.max(Long.parseLong(raw == null ? "1" : raw), 1);
        } catch (NumberFormatException ex) {
            return 1;
        }
    }
}
