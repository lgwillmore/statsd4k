package codes.laurence.statsd4k

import codes.laurence.statsd4k.send.FailedSendHandler
import codes.laurence.statsd4k.send.StatsDSender
import codes.laurence.statsd4k.send.StatsDSenderUDP
import codes.laurence.statsd4k.serialize.StatsDSerializer
import codes.laurence.statsd4k.serialize.StatsDSerializerBase
import codes.laurence.statsd4k.serialize.StatsDSerializerNewRelic
import kotlinx.coroutines.CoroutineDispatcher

class StatsDBuilderContext {
    private var sender: StatsDSender = StatsDSenderUDP()
    private var serializer: StatsDSerializer = StatsDSerializerBase

    fun newRelic() {
        serializer = StatsDSerializerNewRelic
    }

    fun udp(
        host: String = StatsDSenderUDP.DEFAULT_HOST,
        port: Int = StatsDSenderUDP.DEFAULT_PORT,
        dispatcher: CoroutineDispatcher = StatsDSenderUDP.DEFAULT_DISPATCHER,
        channelSize: Int = StatsDSenderUDP.DEFAULT_CHANNEL_SIZE,
        failedSendHandler: FailedSendHandler = { _, _ -> }
    ) {
        sender = StatsDSenderUDP(
            host = host,
            port = port,
            dispatcher = dispatcher,
            channelSize = channelSize,
            failedSendHandler = failedSendHandler
        )
    }

    internal fun build(): StatsD4K {
        return StatsD4KClient(
            sender = sender,
            serialize = serializer
        )
    }
}

fun statsD4K(buildBlock: StatsDBuilderContext.() -> Unit): StatsD4K {
    val context = StatsDBuilderContext()
    context.buildBlock()
    return context.build()
}
