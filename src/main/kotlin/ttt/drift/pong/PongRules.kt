package ttt.drift.pong

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import ttt.drift.Game
import ttt.drift.Rules
import ttt.drift.event.CreateShapeEvent
import ttt.drift.event.MoveShapeEvent
import ttt.drift.geo.*

val pitchArea = Area(500, 1000)
val center = pitchArea.center()
val ballArea = Area(10, 10)
val racketArea = Area(100, 10)
val startingDirection = Coords(10, 10)

class PongRules(id: String, name: String, players: Int, options: JsonObject) : Rules(id, name, players, options) {

    var ballDirection = 1

    override fun start(vertx: Vertx, game: Game) {

        // Prepare Pitch

        val pitchId = nextEventId()
        val pitch = Rectangle(pitchId, area = pitchArea)
        send(CreateShapeEvent(pitchId, pitch))

        val racketBlueId = nextEventId()
        val racketBlue = Rectangle(
            racketBlueId,
            area = racketArea
        )
        send(CreateShapeEvent(racketBlueId, racketBlue))

        val racketRedId = nextEventId()
        val racketRed = Rectangle(
            racketRedId,
            area = racketArea,
            position = Coords( 0, pitchArea.height - racketArea.height )
        )
        send(CreateShapeEvent(racketRedId, racketRed))

        val ballId = nextEventId()
        var ball: Shape = Rectangle(
            ballId,
            pitchArea.center(ballArea),
            startingDirection,
            ballArea
        )
        send(CreateShapeEvent(ballId, ball))

        vertx.setPeriodic(1000) {

            ball = game.move(ball)
            send(MoveShapeEvent(nextEventId(), ball))

            send(BallMoveEvent(nextEventId(), ballDirection))
        }
    }

    override fun play(player: String, move: JsonObject) {
        ballDirection = move.getInteger("direction")
    }

}
