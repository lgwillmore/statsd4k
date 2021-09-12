package codes.laurence.statsd4k.sample

import io.ktor.util.date.*
import kotlin.random.Random

private val random = Random(getTimeMillis())
val DEFAULT_SAMPLER: Sampler = { sampleRate -> sampleRate > random.nextDouble() }
