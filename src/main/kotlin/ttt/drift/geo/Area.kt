package ttt.drift.geo

import io.vertx.core.json.JsonObject

val EMPTY = Area(0, 0)

data class Area(val width: Int, val height: Int) :
    JsonObject(
        mapOf(
            "width" to width,
            "height" to height
        )
    ) {

    fun center(offset: Area = EMPTY) = Coords(width / 2 - offset.width / 2, height / 2 - offset.height / 2)

}