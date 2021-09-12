package codes.laurence.statsd4k.integration

import codes.laurence.statsd4k.StatsD4KClient
import codes.laurence.statsd4k.send.StatsDSenderUDP
import codes.laurence.statsd4k.serialize.StatsDSerializerNewRelic
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
            val client = StatsD4KClient(
                serialize = StatsDSerializerNewRelic,
                sender = StatsDSenderUDP("localhost", 8125)
            )

            client.count("test", 1)
        }
    }
}