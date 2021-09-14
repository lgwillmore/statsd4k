package codes.laurence.statsd4k.integration

import codes.laurence.statsd4k.statsD4K
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

/**
 * This is used in conjunction with the New Relic docker server.
 *
 * Start the server with `./gradlew dockerComposeUp` and set the required api key environment variables.
 */
@Disabled("integration test")
class NewRelicTest {

    private val dispatcher = Executors.newFixedThreadPool(5).asCoroutineDispatcher()

    @Test
    fun sendCountMetric() {
        runBlocking {
            val myStatsD4K = statsD4K {
                newRelic()
                udp()
            }
            List(5) {
                launch(dispatcher) {
                    repeat(2000) {
                        myStatsD4K.count(
                            bucket = "test.metric.bucket",
                            value = 1,
                            sampleRate = 1.0,
                            tags = mapOf(
                                "simple" to null,
                                "key1" to "value1",
                                "key2" to "value2",
                                "key3" to "verylongvalue3yes",
                                "key4" to "verylongvalue4yes",
                            )
                        )
                    }
                }
            }.joinAll()
        }
    }
}
