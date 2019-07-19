package ttt.drift

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import mu.KotlinLogging

class WebVerticle : AbstractVerticle() {

    private val logger = KotlinLogging.logger {}

    override fun start(startFuture: Future<Void>) {

        val server = vertx.createHttpServer()
        val router = Router.router(vertx)
        val sockJSHandler = SockJSHandler.create(vertx)
        val bridgeOptions = BridgeOptions()
            .addInboundPermitted(PermittedOptions().setAddressRegex("matches\\/.*\\/input"))
            .addOutboundPermitted(PermittedOptions().setAddressRegex("matches\\/.*\\/events"))
            .addInboundPermitted(PermittedOptions().setAddress("matches"))
            .addOutboundPermitted(PermittedOptions().setAddress("matches/new"))
        sockJSHandler.bridge(bridgeOptions)

        router.get("/").handler(StaticHandler.create().setCachingEnabled(false))

        router.route("/eventbus/*").handler(sockJSHandler)

        server.requestHandler(router).listen(8080) { ar ->
            if (ar.succeeded()) {
                logger.info { "HTTP server running on port 8080" }
                startFuture.complete()
            } else {
                logger.error(ar.cause()) { "Could not start a HTTP server" }
                startFuture.fail(ar.cause())
            }
        }

    }
}