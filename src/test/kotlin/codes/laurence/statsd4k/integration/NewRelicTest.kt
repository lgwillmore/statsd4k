package codes.laurence.statsd4k.integration

import codes.laurence.statsd4k.statsD4K
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * This is used in conjunction with the New Relic docker server.
 *
 * Start the server with `./gradlew dockerComposeUp` and set the required api key environment variables.
 */
@Disabled("integration test")
class NewRelicTest {

    @Test
    fun sendCountMetric() {
        runBlocking {
            val myStatsD4K = statsD4K {
                newRelic()
                udp()
            }
            myStatsD4K.count(
                bucket = "test.metric",
                value = 1,
                sampleRate = 0.5,
                tags = mapOf(
                    "simple" to null,
                    "key" to "value"
                )
            )
        }
    }
}
