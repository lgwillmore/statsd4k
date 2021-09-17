package codes.laurence.statsd4k

/**
 * A null implementation that does nothing
 */
class StatsD4KNull : StatsD4K {

    override suspend fun count(bucket: String, value: Int, sampleRate: Double, tags: Map<String, String?>) {
        // Do nothing
    }

    override suspend fun time(bucket: String, millis: Long, sampleRate: Double, tags: Map<String, String?>) {
        // Do nothing
    }

    override suspend fun gauge(bucket: String, value: Double, tags: Map<String, String?>) {
        // Do nothing
    }

    override suspend fun set(bucket: String, value: String, tags: Map<String, String?>) {
        // Do nothing
    }
}
