package ttt.drift.geo

import io.vertx.core.json.JsonObject

val ORIGIN = Coords(0, 0)

data class Coords(val x: Int, val y: Int) :
    JsonObject(
        mapOf(
            "x" to x,
            "y" to y
        )
    ) {

    operator fun plus(coords: Coords) = Coords(x + coords.x, y + coords.y)

    fun plusX(offset: Int) = Coords(x + offset, y)

    fun plusY(offset: Int) = Coords(x, y + offset)

}