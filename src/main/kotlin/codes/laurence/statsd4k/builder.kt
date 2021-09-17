package codes.laurence.statsd4k

import codes.laurence.statsd4k.send.FailedSendHandler
import codes.laurence.statsd4k.send.StatsDSender
import codes.laurence.statsd4k.send.StatsDSenderUDP
import codes.laurence.statsd4k.serialize.StatsDSerializer
import codes.laurence.statsd4k.serialize.StatsDSerializerBase
import codes.laurence.statsd4k.serialize.StatsDSerializerNewRelic
import kotlinx.coroutines.CoroutineDispatcher

class UDPBuilderContext {
    var host: String = StatsDSenderUDP.DEFAULT_HOST
    var port: Int = StatsDSenderUDP.DEFAULT_PORT
    var dispatcher: CoroutineDispatcher = StatsDSenderUDP.DEFAULT_DISPATCHER
    var channelSize: Int = StatsDSenderUDP.DEFAULT_CHANNEL_SIZE
    var failedSendHandler: FailedSendHandler = { _, _ -> }

    internal fun build(): StatsDSenderUDP {
        return StatsDSenderUDP(
            host = host,
            port = port,
            dispatcher = dispatcher,
            channelSize = channelSize,
            failedSendHandler = failedSendHandler
        )
    }
}

class StatsDBuilderContext {
    private var sender: StatsDSender = StatsDSenderUDP()
    private var serializer: StatsDSerializer = StatsDSerializerBase

    fun newRelic() {
        serializer = StatsDSerializerNewRelic
    }

    fun udp(
        configureBlock: UDPBuilderContext.() -> Unit = {}
    ) {
        val context = UDPBuilderContext()
        context.configureBlock()
        sender = context.build()
    }

    internal fun build(): StatsD4K {
        return StatsD4KClient(
            sender = sender,
            serialize = serializer
        )
    }
}

fun statsD4K(configureBlock: StatsDBuilderContext.() -> Unit = {}): StatsD4K {
    val context = StatsDBuilderContext()
    context.configureBlock()
    return context.build()
}
