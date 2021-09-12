package codes.laurence.statsd4k.serialize

import assertk.assertThat
import assertk.assertions.isEqualTo
import codes.laurence.statsd4k.All_MESSAGE_TYPES
import codes.laurence.statsd4k.message.Message
import org.junit.jupiter.api.Test

class StatsDSerializerBaseTest {

    private val testObj = StatsDSerializerBase

    @Test
    fun `serialize - sample rate is 1`() {
        All_MESSAGE_TYPES.forEach { inputMessage ->
            val message = when (inputMessage) {
                is Message.Count -> inputMessage.copy(sampleRate = 1.0)
                is Message.Time -> inputMessage.copy(sampleRate = 1.0)
                is Message.Gauge -> inputMessage
                is Message.Set -> inputMessage
            }
            val expected = "${message.bucket}:${message.value}|${message.type}"
            assertThat(testObj(message)).isEqualTo(expected)
        }
    }

    @Test
    fun `serialize - sample rate is less than 1 greater than 0`() {
        val sampleRate = 0.6
        All_MESSAGE_TYPES.forEach { inputMessage ->
            val message = when (inputMessage) {
                is Message.Count -> inputMessage.copy(sampleRate = sampleRate)
                is Message.Time -> inputMessage.copy(sampleRate = sampleRate)
                is Message.Gauge -> null
                is Message.Set -> null
            }
            if (message != null) {
                val expected = "${message.bucket}:${message.value}|${message.type}|@${message.sampleRate}"
                assertThat(testObj(message)).isEqualTo(expected)
            }
        }
    }
}
