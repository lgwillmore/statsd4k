package codes.laurence.statsd4k.sample

import codes.laurence.statsd4k.message.Message

interface Sampler {

    fun <V> sample(message: Message<V>): Message<V>?

}