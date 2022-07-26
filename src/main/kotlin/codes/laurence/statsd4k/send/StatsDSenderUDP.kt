package codes.laurence.statsd4k.send

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.text.toByteArray

/**
 * Used to detect messages are being dropped at client side.
 */
typealias FailedSendHandler = (message: String, exception: Exception?) -> Unit

class StatsDSenderUDP(
    host: String = DEFAULT_HOST,
    port: Int = DEFAULT_PORT,
    dispatcher: CoroutineDispatcher = DEFAULT_DISPATCHER,
    channelSize: Int = DEFAULT_CHANNEL_SIZE,
    private val failedSendHandler: FailedSendHandler = { _, _ -> }
) : StatsDSender {
    companion object {
        const val DEFAULT_HOST = "127.0.0.1"
        const val DEFAULT_PORT = 8125
        const val DEFAULT_CHANNEL_SIZE = 5000
        val DEFAULT_DISPATCHER = Dispatchers.IO
    }

    internal val address = InetSocketAddress(host, port)
    private val scope: CoroutineScope = CoroutineScope(dispatcher + Job())
    private val connection: ConnectedDatagramSocket
    private val bufferChannel = Channel<String>(channelSize)

    init {
        require(channelSize >= 100) { "Please use a channel size >= 100 to avoid excessive message loss" }
        val builder = aSocket(ActorSelectorManager(Dispatchers.IO))
        connection = builder.udp().connect(address)
        runBlocking {
            startSender()
        }
    }

    private suspend fun startSender() {
        with(scope) {
            launch {
                for (message in bufferChannel) {
                    try {
                        val datagram = Datagram(
                            packet = ByteReadPacket(array = message.toByteArray()),
                            address = address
                        )
                        connection.outgoing.send(datagram)
                    } catch (e: Exception) {
                        failedSendHandler("Could not send datagram", e)
                    }
                }
            }
        }
    }

    override suspend fun send(message: String) {
        val result = bufferChannel.trySend(message)
        if (result.isFailure) {
            failedSendHandler("Buffer is full", null)
        }
    }
}
