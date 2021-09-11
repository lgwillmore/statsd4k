package codes.laurence.statsd4k.message

sealed class Message<Value> {
    abstract val type: String
    abstract val bucket: String
    abstract val value: Value
    abstract val sampleRate: Double?
    abstract val tags: Map<String, String?>

    data class Count(
        override val bucket: String,
        override val value: Int,
        override val sampleRate: Double?,
        override val tags: Map<String, String?>,
    ) : Message<Int>() {
        override val type = "c"
    }

    data class Gauge(
        override val bucket: String,
        override val value: Double,
        override val sampleRate: Double?,
        override val tags: Map<String, String?>,
    ) : Message<Double>() {
        override val type = "g"
    }

    data class Time(
        override val bucket: String,
        override val value: Long,
        override val sampleRate: Double?,
        override val tags: Map<String, String?>,
    ) : Message<Long>() {
        override val type = "ms"
    }
}