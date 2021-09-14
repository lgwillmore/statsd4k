package codes.laurence.statsd4k.send

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.net.InetSocketAddress

class StatsDSenderUDP(
    host: String = "127.0.0.1",
    port: Int = 8125,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    channelSize: Int = 5000
) : StatsDSender {
    private val scope: CoroutineScope = CoroutineScope(dispatcher + Job())
    private val out: ByteWriteChannel
    private val bufferChannel = Channel<String>(channelSize)

    init {
        val builder = aSocket(ActorSelectorManager(Dispatchers.IO))
        val connection = builder.udp().connect(InetSocketAddress(host, port))
        out = connection.openWriteChannel(autoFlush = false)
        runBlocking {
            startSender()
        }
    }

    private suspend fun startSender() {
        with(scope) {
            launch {
                for (message in bufferChannel) {
                    out.writeFully(message.toByteArray())
                    out.flush()
                }
            }
        }
    }

    override suspend fun send(message: String) {
        val result = bufferChannel.trySend(message)
        when {
            result.isFailure -> println("Failed")
        }
    }
}
