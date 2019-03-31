package ttt.drift

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.shareddata.AsyncMap
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import java.util.*

private const val MATCHES_KEY = "matches"

class MatchVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(MatchVerticle::class.java)

    override fun start(startFuture: Future<Void>) {
        vertx.eventBus().consumer<String>("matches") { msg ->
            when (msg.headers()["action"]) {
                "list" -> getMatches(Handler { arMap ->
                    if (arMap.succeeded()) {
                        arMap.result().keys { arKeys ->
                            if (arKeys.succeeded()) {
                                msg.reply(json { array(arKeys.result()) })
                            } else {
                                msg.reply(arKeys.cause())
                            }
                        }
                    } else {
                        msg.reply(arMap.cause())
                    }
                })
                "create" -> getMatches(Handler { arMap ->
                    if (arMap.succeeded()) {
                        val uid = UUID.randomUUID().toString()
                        val json = json { obj { "uid" to uid } }
                        arMap.result().put(uid, json) { arPut ->
                            if (arPut.succeeded()) {
                                msg.reply(json)
                            } else {
                                msg.reply(arPut.cause())
                            }
                        }
                    } else {
                        msg.reply(arMap.cause())
                    }
                })
                else -> msg.reply("${msg.body()} not supported!")
            }
        }
        startFuture.complete()
    }

    private fun getMatches(handler: Handler<AsyncResult<AsyncMap<String, JsonObject>>>) {
        vertx.sharedData().getAsyncMap<String, JsonObject>(MATCHES_KEY) { res ->
            if (res.succeeded()) {
                handler.handle(Future.succeededFuture(res.result()))
            } else {
                handler.handle(Future.failedFuture(res.cause()))

            }
        }
    }

}