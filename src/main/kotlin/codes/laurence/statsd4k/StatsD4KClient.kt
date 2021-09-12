package codes.laurence.statsd4k

import codes.laurence.statsd4k.message.Message
import codes.laurence.statsd4k.sample.DEFAULT_SAMPLER
import codes.laurence.statsd4k.sample.Sampler
import codes.laurence.statsd4k.send.StatsDSender
import codes.laurence.statsd4k.serialize.StatsDSerializer
import io.ktor.util.date.*

typealias ExceptionHandler = (exception: Exception) -> Unit

class StatsD4KClient(
    private val serialize: StatsDSerializer,
    private val sender: StatsDSender,
    private val globalTags: Map<String, String?> = emptyMap(),
    private val exceptionHandler: ExceptionHandler = {},
    private val sampler: Sampler = DEFAULT_SAMPLER,
) : StatsD4K {

    override suspend fun count(bucket: String, value: Int, sampleRate: Double, tags: Map<String, String?>) {
        val message = Message.Count(
            bucket = bucket,
            value = value,
            sampleRate = sampleRate,
            tags = tags
        )
        handleMessage(message)
    }

    override suspend fun time(bucket: String, millis: Long, sampleRate: Double, tags: Map<String, String?>) {
        val message = Message.Time(
            bucket = bucket,
            value = millis,
            sampleRate = sampleRate,
            tags = tags
        )
        handleMessage(message)
    }

    override suspend fun <T> timed(
        bucket: String,
        sampleRate: Double,
        tags: Map<String, String?>,
        block: suspend () -> T
    ): T {
        val startTime = getTimeMillis()
        return block().also {
            val endTime = getTimeMillis()
            time(bucket, endTime - startTime, sampleRate, tags)
        }
    }

    override suspend fun gauge(bucket: String, value: Double, tags: Map<String, String?>) {
        val message = Message.Gauge(
            bucket = bucket,
            value = value,
            tags = tags
        )
        handleMessage(message)
    }

    override suspend fun set(bucket: String, value: String, tags: Map<String, String?>) {
        val message = Message.Set(
            bucket = bucket,
            value = value,
            tags = tags
        )
        handleMessage(message)
    }

    internal suspend fun <V> handleMessage(message: Message<V>) {
        try {
            val globalMessage = when (message) {
                is Message.Count -> message.copy(
                    tags = globalTags + message.tags
                )
                is Message.Time -> message.copy(
                    tags = globalTags + message.tags
                )
                is Message.Gauge -> message.copy(
                    tags = globalTags + message.tags
                )
                is Message.Set -> message.copy(
                    tags = globalTags + message.tags
                )
            }
            if (sampler(globalMessage.sampleRate)) {
                sender.send(serialize(globalMessage))
            }
        } catch (e: Exception) {
            exceptionHandler(e)
        }
    }
}