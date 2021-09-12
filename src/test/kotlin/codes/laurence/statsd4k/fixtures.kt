package codes.laurence.statsd4k

import codes.laurence.statsd4k.message.Message
import io.ktor.util.date.*
import kotlin.random.Random

private val random = Random(getTimeMillis())

fun randString() = "generated${randInt()}"

fun randInt() = random.nextInt()

fun randLong() = random.nextLong()

fun randDouble() = random.nextDouble()

fun randTags() = mapOf(
    randString() to randString(),
    randString() to null
)

inline fun <reified T : Message<*>> randMessageTyped(): T {
    return when {
        Message.Time::class == T::class -> Message.Time(randString(), randLong(), randDouble(), randTags())
        Message.Count::class == T::class -> Message.Count(randString(), randInt(), randDouble(), randTags())
        Message.Gauge::class == T::class -> Message.Gauge(randString(), randDouble(), randTags())
        Message.Set::class == T::class -> Message.Set(randString(), randString(), randTags())
        else -> {
            TODO("${T::class} not implemented")
        }
    } as T
}

val All_MESSAGE_TYPES = listOf(
    randMessageTyped<Message.Count>(),
    randMessageTyped<Message.Time>(),
    randMessageTyped<Message.Gauge>(),
    randMessageTyped<Message.Set>(),
)

fun randMessage(): Message<*> {
    return All_MESSAGE_TYPES.random()
}