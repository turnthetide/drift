package ttt.drift

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router

class LobbyVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(LobbyVerticle::class.java)

    private val players = mutableListOf<String>()

    override fun start(startFuture: Future<Void>) {

        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        router.route("/players").handler { routingContext ->
            val response = routingContext.response()
            response.putHeader("content-type", "text/plain")
            response.end(players.joinToString())
        }

        router.route(HttpMethod.POST, "/players/:player").handler { routingContext ->
            val player = routingContext.pathParam("player")
            val response = routingContext.response()
            if (players.contains(player)) {
                response.setStatusCode(401).end("Player $player already present")
            } else {
                players.add(player)
                response.end("Player $player added")
            }
        }

        router.route(HttpMethod.DELETE, "/players/:player").handler { routingContext ->
            val player = routingContext.pathParam("player")
            val response = routingContext.response()
            if (players.contains(player)) {
                players.remove(routingContext.pathParam("player"))
                routingContext.response().end("Player $player removed")
            } else {
                response.setStatusCode(401).end("Player $player not present")
            }
        }

        server.requestHandler(router).listen(8081) { ar ->
            if (ar.succeeded()) {
                logger.info("HTTP server running on port 8081")
                startFuture.complete()
            } else {
                logger.error("Could not start a HTTP server", ar.cause())
                startFuture.fail(ar.cause())
            }
        }

    }
}