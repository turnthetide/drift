package ttt.drift

import io.vertx.core.json.JsonObject
import java.time.ZonedDateTime

abstract class Event(
    id: Long,
    type: String,
    body: JsonObject = JsonObject(),
    vararg properties: Pair<String, Any>,
    time: ZonedDateTime = ZonedDateTime.now()
) :
    JsonObject(
        mapOf(
            "id" to id,
            "type" to type,
            "time" to time.toString(),
            "body" to body,
            *properties
        )
    ) {

    val id: Long
        get() = getLong("id")

    val type: String
        get() = getString("type")

    val time: ZonedDateTime
        get() = ZonedDateTime.parse(getString("time"))

    val body: JsonObject
        get() = getJsonObject("body")
}