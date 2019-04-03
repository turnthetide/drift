package ttt.drift

import io.quarkus.runtime.StartupEvent
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import javax.enterprise.event.Observes
import javax.inject.Inject

class Drift {

    private val logger = LoggerFactory.getLogger(Drift::class.java)

    @Inject
    lateinit var vertx: Vertx

    fun onStart(@Observes event: StartupEvent) {
        vertx.deployVerticle(MatchVerticle())
        vertx.deployVerticle(WebVerticle())
    }

}
