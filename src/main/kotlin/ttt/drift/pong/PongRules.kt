package ttt.drift.pong

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import ttt.drift.Rules

class PongRules(id: String, name: String, players: Int, options: JsonObject) : Rules(id, name, players, options) {

    var ballDirection = 1

    override fun start(vertx: Vertx) {
        vertx.setPeriodic(100) {
            send(BallMoveEvent(nextEventId(), ballDirection))
        }
    }

    override fun play(player: String, move: JsonObject) {
        ballDirection = move.getInteger("direction")
    }

}
