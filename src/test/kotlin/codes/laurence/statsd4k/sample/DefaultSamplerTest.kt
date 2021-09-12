package codes.laurence.statsd4k.sample

import assertk.assertThat
import assertk.assertions.isBetween
import org.junit.jupiter.api.Test

class DefaultSamplerTest {

    val testObj = DEFAULT_SAMPLER

    @Test
    fun `sample - 1`() {
        sampleHelper(
            sampleRate = 1.0,
            expectedMin = 1.0,
            expectedMax = 1.0
        )
    }

    @Test
    fun `sample - greater than 0 less than 1`() {
        sampleHelper(
            sampleRate = 0.6,
            expectedMin = 0.55,
            expectedMax = 0.65
        )
    }

    @Test
    fun `sample - greater 0 or less`() {
        sampleHelper(
            sampleRate = 0.0,
            expectedMin = 0.0,
            expectedMax = 0.0
        )
    }

    private fun sampleHelper(sampleRate: Double, expectedMin: Double, expectedMax: Double) {
        val samples = 1000
        var count = 0
        repeat(samples) {
            if (testObj(sampleRate)) count++
        }
        assertThat(count.toDouble() / samples.toDouble()).isBetween(expectedMin, expectedMax)
    }

}