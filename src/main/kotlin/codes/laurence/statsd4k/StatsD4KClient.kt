package codes.laurence.statsd4k

import codes.laurence.statsd4k.message.Message
import codes.laurence.statsd4k.sample.Sampler
import codes.laurence.statsd4k.send.StatsDSender
import codes.laurence.statsd4k.serialize.StatsDSerializer

typealias ExceptionHandler = (exception: Exception) -> Unit

class StatsD4KClient(
    private val serialize: StatsDSerializer,
    private val sender: StatsDSender,
    private val globalTags: Map<String, String?> = emptyMap(),
    private val globalSampleRate: Double? = null,
    private val sampler: Sampler,
    private val exceptionHandler: ExceptionHandler = {}
) : StatsD4K {

    override suspend fun count(bucket: String, value: Int, sampleRate: Double?, tags: Map<String, String?>) {
        val message = Message.Count(
            bucket = bucket,
            value = value,
            sampleRate = sampleRate,
            tags = tags
        )
        handleMessage(message)
    }

    override suspend fun time(bucket: String, millis: Long, sampleRate: Double?, tags: Map<String, String?>) {
        val message = Message.Time(
            bucket = bucket,
            value = millis,
            sampleRate = sampleRate,
            tags = tags
        )
        handleMessage(message)
    }

    override suspend fun gauge(bucket: String, value: Double, sampleRate: Double?, tags: Map<String, String?>) {
        val message = Message.Gauge(
            bucket = bucket,
            value = value,
            sampleRate = sampleRate,
            tags = tags
        )
        handleMessage(message)
    }

    internal suspend fun <V> handleMessage(message: Message<V>) {
        try {
            sampler.sample(message)?.let { sampled ->
                sender.send(serialize(sampled))
            }
        } catch (e: Exception) {
            exceptionHandler(e)
        }
    }
}