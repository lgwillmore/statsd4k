package codes.laurence.statsd4k.send

import assertk.assertThat
import assertk.assertions.containsOnly
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress

internal class StatsDSenderUDPTest {

    private val host = "127.0.0.1"
    private val port = 2323

    @Test
    fun send() {
        runBlocking {
            val testObj = buildSender()
            val listener = buildListener()

            val messages = List(10) { index -> "foo$index" }
            messages.forEach {
                launch {
                    testObj.send(it)
                }
            }

            val received = mutableListOf<String?>()

            launch {
                repeat(messages.size) {
                    received.add(listener.receive().packet.readUTF8Line())
                }
            }.join()

            assertThat(received).containsOnly(*messages.toTypedArray())

        }
    }

    private fun buildSender(): StatsDSenderUDP = StatsDSenderUDP(host, port)

    private fun buildListener() =
        aSocket(ActorSelectorManager(Dispatchers.IO)).udp().bind(InetSocketAddress(host, port))
}