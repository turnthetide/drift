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

private val logger = LoggerFactory.getLogger(WebVerticle::class.java)

class WebVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {

        val server = vertx.createHttpServer()
        val router = Router.router(vertx)
        val sockJSHandler = SockJSHandler.create(vertx)
        val bridgeOptions = BridgeOptions()
            .addInboundPermitted(PermittedOptions().setAddress("input"))
            .addOutboundPermitted(PermittedOptions().setAddress("events"))
            .addInboundPermitted(PermittedOptions().setAddress("matches"))
            .addOutboundPermitted(PermittedOptions().setAddress("matches/new"))
        sockJSHandler.bridge(bridgeOptions)

        router.get("/").handler(StaticHandler.create().setCachingEnabled(false))

        router.route("/eventbus/*").handler(sockJSHandler)

        vertx.eventBus().consumer<String>("input") { msg ->
            vertx.eventBus().publish("events", msg.body())
        }

        vertx.setPeriodic(1000) {
            vertx.eventBus().publish("events", if (Random.nextBoolean()) "/" else "\\")
        }

        server.requestHandler(router).listen(8080) { ar ->
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