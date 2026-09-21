package research.reactive;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class ReactiveVertxApplication extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new ReactiveVertxApplication());
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/health").handler(ctx -> ctx.response().end("UP"));
        router.get("/work").handler(ctx -> {
            long delayMs = parseDelay(ctx.request().getParam("delayMs"));
            // Non-blocking: the event loop is released while the timer is pending.
            vertx.setTimer(delayMs, ignored -> ctx.response().end("reactive-ok"));
        });
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router).listen(8081)
              .onSuccess(ignored -> startPromise.complete())
              .onFailure(startPromise::fail);
    }

    private static long parseDelay(String raw) {
        try {
            return Math.min(Math.max(Long.parseLong(raw == null ? "50" : raw), 0), 5000);
        } catch (NumberFormatException ex) {
            return 50;
        }
    }
}