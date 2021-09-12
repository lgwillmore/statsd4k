package codes.laurence.statsd4k.serialize

import kotlin.math.max

val StatsDSerializerBase: StatsDSerializer = { message ->
    val base = "${message.bucket}:${message.value}|${message.type}"
    if (message.sampleRate >= 1.0) {
        base
    } else {
        "$base|@${max(0.0, message.sampleRate)}"
    }
}