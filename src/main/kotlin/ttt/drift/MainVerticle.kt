package ttt.drift

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.logging.LoggerFactory

class MainVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

    override fun start(startFuture: Future<Void>) {

        vertx.deployVerticle("ttt.drift.MatchVerticle") {
            vertx.deployVerticle("ttt.drift.WebVerticle") {
                startFuture.complete()
            }
        }

    }

}
