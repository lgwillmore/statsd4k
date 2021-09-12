package codes.laurence.statsd4k.send

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import java.net.InetSocketAddress

class StatsDSenderUDP(
    host: String = "127.0.0.1",
    port: Int = 8125
) : StatsDSender {
    private val out: ByteWriteChannel

    init {
        val builder = aSocket(ActorSelectorManager(Dispatchers.IO))
        val connection = builder.udp().connect(InetSocketAddress(host, port))
        out = connection.openWriteChannel(autoFlush = true)
    }

    override suspend fun send(message: String) {
        out.writeFully(message.toByteArray())
    }
}