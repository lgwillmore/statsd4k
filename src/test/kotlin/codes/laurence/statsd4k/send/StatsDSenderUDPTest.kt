package codes.laurence.statsd4k.send

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isSuccess
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

internal class StatsDSenderUDPTest {

    private val host = "127.0.0.1"
    private val port = 2323
    private val dispatcher = Executors.newFixedThreadPool(5).asCoroutineDispatcher()

    @Test
    fun `initialise with bad channel size`() {
        assertThat { StatsDSenderUDP(channelSize = 0) }.isFailure().isInstanceOf(IllegalArgumentException::class)
        assertThat { StatsDSenderUDP(channelSize = -1) }.isFailure().isInstanceOf(IllegalArgumentException::class)
        assertThat { StatsDSenderUDP(channelSize = 99) }.isFailure().isInstanceOf(IllegalArgumentException::class)
    }

    @Test
    fun `initialise - valid`() {
        assertThat { StatsDSenderUDP(channelSize = 100) }.isSuccess()
    }

    @Test
    fun send() {
        runBlocking {
            val testObj = StatsDSenderUDP(host, port)
            val listener = buildListener()

            var sentCount = 0
            val messages = List(100) { index -> "foo$index" }
            val notReceived = mutableSetOf<String>()
            notReceived.addAll(messages)
            val unexpectedMessages = mutableListOf<String>()

            val receiver = launch {
                var received = 0
                while (received < messages.size) {
                    withTimeout(5000) {
                        val message = listener.receive().packet.readUTF8Line()
                        received++
                        if (!notReceived.remove(message)) {
                            unexpectedMessages.add(message!!)
                        }
                    }
                }
            }
            messages.forEach {
                launch(dispatcher) {
                    testObj.send(it)
                    sentCount++
                }
            }
            receiver.join()

            assertThat(unexpectedMessages).isEmpty()
            assertThat(notReceived).isEmpty()
        }
    }

    private fun buildListener() =
        aSocket(ActorSelectorManager(Dispatchers.IO)).udp().bind(InetSocketAddress(host, port))
}
