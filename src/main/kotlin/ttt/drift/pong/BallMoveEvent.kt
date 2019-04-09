package ttt.drift.pong

import ttt.drift.Event

class BallMoveEvent(id: Long, move: Int) : Event(id, "BallMoveEvent", "move" to move)
