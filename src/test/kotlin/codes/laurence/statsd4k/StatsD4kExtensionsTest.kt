package codes.laurence.statsd4k

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class StatsD4kExtensionsTest {

    @Test
    fun timed() {
        runBlocking {
            val bucket = randString()
            val sampleRate = randDouble()
            val tags = randTags()
            val testObj: StatsD4K = mockk()
            coEvery { testObj.time(any(), any(), any(), any()) } returns Unit

            val t = testObj.timed(bucket, sampleRate, tags) {
                delay(20)
                "foo"
            }

            assertThat(t).isEqualTo("foo")

            coVerify { testObj.time(bucket, any(), sampleRate, tags) }
        }
    }

    @Test
    fun increment() {
        runBlocking {
            val bucket = randString()
            val sampleRate = randDouble()
            val tags = randTags()
            val testObj: StatsD4K = mockk()
            coEvery { testObj.count(any(), any(), any(), any()) } returns Unit

            testObj.increment(bucket, sampleRate, tags)

            coVerify { testObj.count(bucket, 1, sampleRate, tags) }
        }
    }

    @Test
    fun decrement() {
        runBlocking {
            val bucket = randString()
            val sampleRate = randDouble()
            val tags = randTags()
            val testObj: StatsD4K = mockk()
            coEvery { testObj.count(any(), any(), any(), any()) } returns Unit

            testObj.decrement(bucket, sampleRate, tags)

            coVerify { testObj.count(bucket, -1, sampleRate, tags) }
        }
    }
}
