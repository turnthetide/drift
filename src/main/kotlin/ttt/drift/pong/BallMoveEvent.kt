package ttt.drift.pong

import io.vertx.core.json.JsonObject
import ttt.drift.Event

class BallMoveEvent(id: Long, move: Int) : Event(id, "BallMoveEvent", JsonObject(), "move" to move)
