package codes.laurence.statsd4k.sample

import io.ktor.util.date.*

typealias Sampler = (sampleRate: Double) -> Boolean
