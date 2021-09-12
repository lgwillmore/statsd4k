package codes.laurence.statsd4k

import codes.laurence.statsd4k.send.StatsDSender
import codes.laurence.statsd4k.send.StatsDSenderUDP
import codes.laurence.statsd4k.serialize.StatsDSerializer
import codes.laurence.statsd4k.serialize.StatsDSerializerBase
import codes.laurence.statsd4k.serialize.StatsDSerializerNewRelic

class StatsDBuilderContext() {
    private var sender: StatsDSender = StatsDSenderUDP()
    private var serializer: StatsDSerializer = StatsDSerializerBase

    fun newRelic() {
        serializer = StatsDSerializerNewRelic
    }

    fun udp(host: String = "127.0.0.1", port: Int = 8125) {
        sender = StatsDSenderUDP(host, port)
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
