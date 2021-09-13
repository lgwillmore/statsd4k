package codes.laurence.statsd4k

import io.ktor.util.date.*

suspend fun <T> StatsD4K.timed(
    bucket: String,
    sampleRate: Double = 1.0,
    tags: Map<String, String?> = emptyMap(),
    block: suspend () -> T
): T {
    val startTime = getTimeMillis()
    return block().also {
        val endTime = getTimeMillis()
        time(bucket, endTime - startTime, sampleRate, tags)
    }
}

suspend fun StatsD4K.increment(
    bucket: String,
    sampleRate: Double = 1.0,
    tags: Map<String, String?> = emptyMap(),
) {
    count(bucket, 1, sampleRate, tags)
}

suspend fun StatsD4K.decrement(
    bucket: String,
    sampleRate: Double = 1.0,
    tags: Map<String, String?> = emptyMap(),
) {
    count(bucket, -1, sampleRate, tags)
}
