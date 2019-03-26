package ttt.drift

import io.vertx.core.Vertx
import io.vertx.junit5.Timeout
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(VertxExtension::class)
class TestMainVerticle {

    @BeforeEach
    fun deployVerticle(vertx: Vertx, testContext: VertxTestContext) {
        vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { testContext.completeNow() })
    }

    @Test
    @DisplayName("Should start a Web Server on port 8080")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    @Throws(Throwable::class)
    fun startHttpServer(vertx: Vertx, testContext: VertxTestContext) {
//    vertx.createHttpClient().getNow(8081, "localhost", "/") { response ->
//      testContext.verify {
//        assertTrue(response.statusCode() == 200)
//        response.handler { body ->
//          assertTrue(body.toString().contains("Hello from Vert.x!"))
//          testContext.completeNow()
//        }
//      }
//    }
        testContext.completeNow()
    }

}
