package ttt.drift

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.ext.web.sstore.LocalSessionStore

class MainVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startFuture: Future<Void>) {
    val server = vertx.createHttpServer()

    val router = Router.router(vertx)

    router.route().handler(CookieHandler.create())
    router.route().handler(BodyHandler.create())
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)))

    val sockJSHandler = SockJSHandler.create(vertx)
    val bridgeOptions = BridgeOptions()
      .addInboundPermitted(PermittedOptions().setAddress("input"))
      .addOutboundPermitted(PermittedOptions().setAddress("events"))
    sockJSHandler.bridge(bridgeOptions)
    router.route("/eventbus/*").handler(sockJSHandler)

    router.get("/app/*").handler(StaticHandler.create().setCachingEnabled(false))
    router.get("/").handler { context -> context.reroute("/app/index.html") }

    vertx.eventBus().consumer<String>("input") { msg ->
      vertx.eventBus().publish("events", msg.body())
    }

    server
      .requestHandler(router)
      .listen(8080) {
          ar ->
        if (ar.succeeded()) {
          logger.info("HTTP server running on port 8080")
          startFuture.complete()
        } else {
          logger.error("Could not start a HTTP server", ar.cause())
          startFuture.fail(ar.cause())
        }
      }
  }

}
