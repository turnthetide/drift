package ttt.drift

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import ttt.drift.pong.PongRules
import java.util.*

fun createMatch(id: String, name: String, players: Int, options: JsonObject) = PongRules(id, name, players, options)

abstract class Rules(val id: String, val name: String, val players: Int, val options: JsonObject) {

    val invites: List<String> = 1.rangeTo(players).map { UUID.randomUUID().toString() }
    val eventListeners = mutableListOf<RulesEventListener>()
    var eventCounter: Long = 0

    abstract fun start(vertx: Vertx)

    abstract fun play(player: String, move: JsonObject)

    protected fun send(event: Event) {
        eventListeners.forEach { el -> el.event(event) }
    }

    protected fun nextEventId() = eventCounter++

}