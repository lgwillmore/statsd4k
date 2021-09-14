package codes.laurence.statsd4k.send

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.util.concurrent.Executors

internal class StatsDSenderUDPTest {

    private val host = "127.0.0.1"
    private val port = 2323
    private val dispatcher = Executors.newFixedThreadPool(5).asCoroutineDispatcher()

    @Test
    fun send() {
        runBlocking {
            val testObj = buildSender()
            val listener = buildListener()

            var sentCount = 0
            val messages = List(100) { index -> "foo$index" }
            val notReceived = mutableSetOf<String>()
            notReceived.addAll(messages)
            val unexpectedMessages = mutableListOf<String>()

            val receiver = launch {
                var received = 0
                while(received < messages.size) {
                    withTimeout(2000){
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

    private fun buildSender(): StatsDSenderUDP = StatsDSenderUDP(host, port)

    private fun buildListener() =
        aSocket(ActorSelectorManager(Dispatchers.IO)).udp().bind(InetSocketAddress(host, port))
}
