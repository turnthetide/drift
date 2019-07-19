package ttt.drift

import mu.KotlinLogging
import ttt.drift.event.CreateShapeEvent
import ttt.drift.event.MoveShapeEvent
import ttt.drift.geo.Shape


class Game : RulesEventListener() {

    private val logger = KotlinLogging.logger {}

    override fun event(event: Event) = when {
        event.type == CreateShapeEvent.TYPE -> {}
        event.type == MoveShapeEvent.TYPE -> {}
        else -> logger.warn { "Event $event not supported!" }
    }

    fun move() {
//        val newPosition = position + direction
//        return Shape(id, newPosition, direction, vertices)
    }
}
