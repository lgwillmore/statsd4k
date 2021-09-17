package codes.laurence.statsd4k

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isSameAs
import codes.laurence.statsd4k.send.StatsDSenderUDP
import codes.laurence.statsd4k.serialize.StatsDSerializerBase
import codes.laurence.statsd4k.serialize.StatsDSerializerNewRelic
import org.junit.jupiter.api.Test

class StatsD4kBuilderTest {

    @Test
    fun default() {
        val result = statsD4K()
        assertThat(result).isInstanceOf(StatsD4KClient::class).given {
            assertThat(it.sender).isInstanceOf(StatsDSenderUDP::class)
            assertThat(it.serialize).isSameAs(StatsDSerializerBase)
        }
    }

    @Test
    fun `serializer - new relic`() {
        val result = statsD4K {
            newRelic()
        }
        assertThat(result).isInstanceOf(StatsD4KClient::class).given {
            assertThat(it.serialize).isSameAs(StatsDSerializerNewRelic)
        }
    }

    @Test
    fun `sender - udp`() {
        val result = statsD4K {
            udp {
                port = 1234
            }
        }
        assertThat(result).isInstanceOf(StatsD4KClient::class).given { client ->
            assertThat(client.sender).isInstanceOf(StatsDSenderUDP::class).given { sender ->
                assertThat(sender.address.port).isEqualTo(1234)
            }
        }
    }
}
