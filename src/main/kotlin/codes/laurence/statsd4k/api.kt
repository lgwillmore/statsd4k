package codes.laurence.statsd4k

interface StatsD4K {

    suspend fun count(
        bucket: String,
        value: Int = 1,
        sampleRate: Double? = null,
        tags: Map<String, String?> = emptyMap(),
    )

    suspend fun time(
        bucket: String,
        millis: Long,
        sampleRate: Double? = null,
        tags: Map<String, String?> = emptyMap(),
    )

    suspend fun gauge(
        bucket: String,
        value: Double,
        sampleRate: Double? = null,
        tags: Map<String, String?> = emptyMap(),
    )
}