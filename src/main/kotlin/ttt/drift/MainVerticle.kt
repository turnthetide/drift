package ttt.drift

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import kotlin.random.Random

class MainVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

    override fun start(startFuture: Future<Void>) {

        vertx.deployVerticle("ttt.drift.LobbyVerticle")

        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        val sockJSHandler = SockJSHandler.create(vertx)
        val bridgeOptions = BridgeOptions()
            .addInboundPermitted(PermittedOptions().setAddress("input"))
            .addOutboundPermitted(PermittedOptions().setAddress("events"))
        sockJSHandler.bridge(bridgeOptions)
        router.route("/eventbus/*").handler(sockJSHandler)

        router.get("/").handler(StaticHandler.create().setCachingEnabled(false))

        vertx.eventBus().consumer<String>("input") { msg ->
            vertx.eventBus().publish("events", msg.body())
        }

        vertx.setPeriodic(100) {
            vertx.eventBus().publish("events", if (Random.nextBoolean()) "/" else "\\")
        }

        server
            .requestHandler(router)
            .listen(8080) { ar ->
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
