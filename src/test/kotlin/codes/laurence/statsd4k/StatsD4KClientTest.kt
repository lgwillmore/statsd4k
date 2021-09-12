package codes.laurence.statsd4k

import codes.laurence.statsd4k.message.Message
import codes.laurence.statsd4k.sample.Sampler
import codes.laurence.statsd4k.send.StatsDSender
import codes.laurence.statsd4k.serialize.StatsDSerializer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.IOException

@ExtendWith(MockKExtension::class)
internal class StatsD4KClientTest {

    private val bucket = randString()
    private val sampleRate = randDouble()
    private val serialized = randString()
    private val tags = randTags()

    @MockK
    private lateinit var serializerMock: StatsDSerializer

    @MockK
    private lateinit var samplerMock: Sampler

    @MockK
    private lateinit var senderMock: StatsDSender

    @MockK(relaxed = true)
    private lateinit var exceptionHandlerMock: ExceptionHandler

    @Test
    fun count() {
        runBlocking {
            val value = randInt()
            val expectedMessage = Message.Count(
                bucket = bucket,
                value = value,
                sampleRate = sampleRate,
                tags = tags
            )
            val testObj: StatsD4KClient = spyk(buildTestObj())
            coEvery { testObj.handleMessage(expectedMessage) } returns Unit

            testObj.count(bucket, value, sampleRate, tags)

            coVerify { testObj.handleMessage(expectedMessage) }
        }
    }

    @Test
    fun time() {
        runBlocking {
            val value = randLong()
            val expectedMessage = Message.Time(
                bucket = bucket,
                value = value,
                sampleRate = sampleRate,
                tags = tags
            )
            val testObj: StatsD4KClient = spyk(buildTestObj())
            coEvery { testObj.handleMessage(expectedMessage) } returns Unit

            testObj.time(bucket, value, sampleRate, tags)

            coVerify { testObj.handleMessage(expectedMessage) }
        }
    }

    @Test
    fun gauge() {
        runBlocking {
            val value = randDouble()
            val expectedMessage = Message.Gauge(
                bucket = bucket,
                value = value,
                tags = tags
            )
            val testObj: StatsD4KClient = spyk(buildTestObj())
            coEvery { testObj.handleMessage(expectedMessage) } returns Unit

            testObj.gauge(bucket, value, tags)

            coVerify { testObj.handleMessage(expectedMessage) }
        }
    }

    @Test
    fun set() {
        runBlocking {
            val value = randString()
            val expectedMessage = Message.Set(
                bucket = bucket,
                value = value,
                tags = tags
            )
            val testObj: StatsD4KClient = spyk(buildTestObj())
            coEvery { testObj.handleMessage(expectedMessage) } returns Unit

            testObj.set(bucket, value, tags)

            coVerify { testObj.handleMessage(expectedMessage) }
        }
    }

    @Test
    fun handleMessage() {
        runBlocking {
            val message = randMessage()
            val testObj = buildTestObj()

            coEvery { samplerMock.invoke(message.sampleRate) } returns true
            coEvery { serializerMock.invoke(message) } returns serialized
            coEvery { senderMock.send(serialized) } returns Unit

            testObj.handleMessage(message)

            coVerify { senderMock.send(serialized) }
        }
    }

    @Test
    fun `handleMessage - global tags`() {
        runBlocking {
            val globalTags = randTags()
            val testObj = buildTestObj(globalTags = globalTags)
            All_MESSAGE_TYPES.forEach { message ->
                val expectedGlobalTagMessage = when (message) {
                    is Message.Count -> message.copy(tags = message.tags + globalTags)
                    is Message.Gauge -> message.copy(tags = message.tags + globalTags)
                    is Message.Time -> message.copy(tags = message.tags + globalTags)
                    is Message.Set -> message.copy(tags = message.tags + globalTags)
                }

                coEvery { samplerMock.invoke(expectedGlobalTagMessage.sampleRate) } returns true
                coEvery { serializerMock.invoke(expectedGlobalTagMessage) } returns serialized
                coEvery { senderMock.send(serialized) } returns Unit

                testObj.handleMessage(message)

                coVerify { senderMock.send(serialized) }
            }
        }
    }

    @Test
    fun `handleMessage - not sampled`() {
        runBlocking {
            val message = randMessage()
            val testObj = buildTestObj()

            coEvery { samplerMock.invoke(message.sampleRate) } returns false
            coEvery { serializerMock.invoke(any()) } returns serialized
            coEvery { senderMock.send(serialized) } returns Unit

            testObj.handleMessage(message)

            coVerify(exactly = 0) { senderMock.send(serialized) }
        }
    }

    @Test
    fun `handleMessage - error`() {
        runBlocking {
            val message = randMessage()
            val testObj = buildTestObj()
            val exception = IOException()

            coEvery { samplerMock.invoke(message.sampleRate) } throws exception
            coEvery { serializerMock.invoke(message) } returns serialized
            coEvery { senderMock.send(serialized) } returns Unit

            testObj.handleMessage(message)

            coVerify(exactly = 0) { senderMock.send(serialized) }
            coVerify { exceptionHandlerMock.invoke(exception) }
        }
    }

    private fun buildTestObj(
        globalTags: Map<String, String?> = emptyMap()
    ): StatsD4KClient {
        return StatsD4KClient(
            serialize = serializerMock,
            sender = senderMock,
            sampler = samplerMock,
            globalTags = globalTags,
            exceptionHandler = exceptionHandlerMock
        )
    }
}
