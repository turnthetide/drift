package ttt.drift.event

import ttt.drift.Event
import ttt.drift.geo.Rectangle
import ttt.drift.geo.Shape

class MoveShapeEvent(id: Long, shape: Shape) : Event(id, TYPE, shape) {

    companion object {
        const val TYPE = "MoveShapeEvent"
    }

}
