package ttt.drift

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.AsyncMap
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import java.util.*

const val MATCHES_KEY = "matches"

class MatchesVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {
        vertx.eventBus().consumer<String>(MATCHES_KEY) { msg ->
            val body = JsonObject(msg.body())
            val action = body.getString("action") ?: ""
            when (action) {
                "list" -> getMatches(listMatches(msg))
                "create" -> getMatches(createMatch(msg, body))
                else -> msg.reply("Action $action not supported!")
            }
        }
        startFuture.complete()
    }

    private fun createMatch(
        msg: Message<String>,
        body: JsonObject
    ): (AsyncResult<AsyncMap<String, JsonObject>>) -> Unit = { arMap ->
        when {
            arMap.succeeded() -> {

                val id = UUID.randomUUID().toString()
                val name = body.getString("name")
                val players = body.getInteger("players")
                val options = body.getJsonObject("options")
                val match = createMatch(id, name, players, options)

                vertx.deployVerticle(MatchVerticle(id, match))

                val json = json { obj("id" to id, "name" to name, "players" to players, "options" to options) }
                val jsonMatch = json.copy()
                jsonMatch.put("invites", JsonArray(match.invites))

                arMap.result().put(id, json) { arPut ->
                    if (arPut.succeeded()) {
                        msg.reply(jsonMatch)
                        vertx.eventBus().publish("$MATCHES_KEY/new", json)
                    } else {
                        msg.reply(arPut.cause())
                    }
                }
            }
            else -> msg.reply(arMap.cause())
        }
    }

    private fun listMatches(msg: Message<String>): (AsyncResult<AsyncMap<String, JsonObject>>) -> Unit = { arMap ->
        when {
            arMap.succeeded() -> arMap.result().keys { arKeys ->
                if (arKeys.succeeded()) {
                    msg.reply(json { array(arKeys.result()) })
                } else {
                    msg.reply(arKeys.cause())
                }
            }
            else -> msg.reply(arMap.cause())
        }
    }

    private fun getMatches(handler: (AsyncResult<AsyncMap<String, JsonObject>>) -> Unit) {
        vertx.sharedData().getAsyncMap<String, JsonObject>(MATCHES_KEY) { res ->
            if (res.succeeded()) {
                handler(Future.succeededFuture(res.result()))
            } else {
                handler(Future.failedFuture(res.cause()))
            }
        }
    }

}