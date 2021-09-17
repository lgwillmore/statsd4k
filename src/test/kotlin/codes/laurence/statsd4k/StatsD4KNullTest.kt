package codes.laurence.statsd4k

import assertk.assertThat
import assertk.assertions.isSuccess
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class StatsD4KNullTest {

    private val testObj = StatsD4KNull()

    @Test
    fun count() {
        runBlocking {
            assertThat {
                testObj.count(randString(), randInt(), randDouble(), randTags())
            }.isSuccess()
        }
    }

    @Test
    fun time() {
        runBlocking {
            assertThat {
                testObj.time(randString(), randLong(), randDouble(), randTags())
            }.isSuccess()
        }
    }

    @Test
    fun gauge() {
        runBlocking {
            assertThat {
                testObj.gauge(randString(), randDouble(), randTags())
            }.isSuccess()
        }
    }

    @Test
    fun set() {
        runBlocking {
            assertThat {
                testObj.set(randString(), randString(), randTags())
            }.isSuccess()
        }
    }
}
