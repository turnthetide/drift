package ttt.drift.geo

import io.vertx.core.json.JsonObject
import ttt.drift.Game

open class Shape(
    val id: Long,
    val position: Coords = ORIGIN,
    val direction: Coords = ORIGIN,
    val vertices: List<Coords>
) :
    JsonObject(
        mapOf(
            "id" to id,
            "position" to position,
            "direction" to direction,
            "vertices" to vertices
        )
    ) {


}
