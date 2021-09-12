package codes.laurence.statsd4k.sample

import io.ktor.util.date.*
import kotlin.random.Random

typealias Sampler = (sampleRate: Double) -> Boolean
