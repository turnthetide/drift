package ttt.drift

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import mu.KotlinLogging

class MatchVerticle(val id: String, private val rules: Rules) : AbstractVerticle() {

    private val logger = KotlinLogging.logger {}
    private val events = mutableListOf<Event>()

    override fun start(startFuture: Future<Void>) {

        val game = Game()
        rules.eventListeners += game
        rules.eventListeners += EventBusEventListener()
        rules.eventListeners += StackEventListener()
        rules.eventListeners += LoggerEventListener()

        vertx.eventBus().consumer<String>("$MATCHES_KEY/$id/input") { msg ->
            val body = JsonObject(msg.body())
            val player = body.getString("player")
            val move = body.getJsonObject("move")
            logger.debug { log("Player $player move $move") }
            rules.play(player, move)
        }

        vertx.eventBus().consumer<String>("$MATCHES_KEY/$id/events/all") { msg ->
            msg.reply(json { array(events) })
        }

        rules.start(vertx, game)
        logger.info { log("Match Started!") }

        startFuture.complete()
    }

    override fun stop() {
        logger.info { log("Match Ended!") }
    }

    private fun log(msg: String) = "[${rules.name}] [$id] $msg"

    inner class StackEventListener : RulesEventListener() {
        override fun event(event: Event) {
            events += event
        }
    }

    inner class LoggerEventListener : RulesEventListener() {
        override fun event(event: Event) {
            logger.info { log("$event") }
        }
    }

    inner class EventBusEventListener : RulesEventListener() {
        override fun event(event: Event) {
            logger.debug { log("Event $event") }
            vertx.eventBus().publish("$MATCHES_KEY/$id/events", event)
        }
    }

}