package ttt.drift

import io.quarkus.runtime.StartupEvent
import io.vertx.core.Vertx
import mu.KotlinLogging
import javax.enterprise.event.Observes
import javax.inject.Inject

class Drift {

    val logger = KotlinLogging.logger { }

    @Inject
    lateinit var vertx: Vertx

    fun onStart(@Observes event: StartupEvent) {
        vertx.deployVerticle(MatchesVerticle())
        vertx.deployVerticle(WebVerticle())
    }

}
