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
}