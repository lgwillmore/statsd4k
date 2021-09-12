package codes.laurence.statsd4k

interface StatsD4K {

    suspend fun count(
        bucket: String,
        value: Int = 1,
        sampleRate: Double = 1.0,
        tags: Map<String, String?> = emptyMap(),
    )

    suspend fun time(
        bucket: String,
        millis: Long,
        sampleRate: Double = 1.0,
        tags: Map<String, String?> = emptyMap(),
    )

    suspend fun <T> timed(
        bucket: String,
        sampleRate: Double = 1.0,
        tags: Map<String, String?> = emptyMap(),
        block: suspend () -> T
    ): T

    suspend fun gauge(
        bucket: String,
        value: Double,
        tags: Map<String, String?> = emptyMap(),
    )
}