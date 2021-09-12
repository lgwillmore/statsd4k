package codes.laurence.statsd4k.serialize

import assertk.assertThat
import assertk.assertions.isEqualTo
import codes.laurence.statsd4k.message.Message
import codes.laurence.statsd4k.randMessageTyped
import org.junit.jupiter.api.Test

class StatsDSerializerNewRelicTest {

    private val testObj = StatsDSerializerNewRelic

    @Test
    fun `serialise - no tags`() {
        val message = randMessageTyped<Message.Count>().copy(tags = emptyMap())
        val expected = StatsDSerializerBase(message)
        assertThat(testObj(message)).isEqualTo(expected)
    }

    @Test
    fun `serialise - tags`() {
        val message = randMessageTyped<Message.Count>().copy(
            tags = mapOf(
                "foo" to null,
                "wiz" to "bang"
            )
        )
        var expectedTagSection = "|#"
        message.tags.forEach { (t, u) ->
            expectedTagSection += if (u == null) {
                "$t,"
            } else {
                "$t:$u,"
            }
        }
        val expected = "${StatsDSerializerBase(message)}$expectedTagSection"
        assertThat(testObj(message)).isEqualTo(expected)
    }
}
