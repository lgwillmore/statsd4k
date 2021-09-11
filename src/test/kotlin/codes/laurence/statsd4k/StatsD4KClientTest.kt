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
                sampleRate = sampleRate,
                tags = tags
            )
            val testObj: StatsD4KClient = spyk(buildTestObj())
            coEvery { testObj.handleMessage(expectedMessage) } returns Unit

            testObj.gauge(bucket, value, sampleRate, tags)

            coVerify { testObj.handleMessage(expectedMessage) }
        }
    }

    @Test
    fun handleMessage() {
        runBlocking {
            val message = randMessage()
            val sampledMessage = randMessage()
            val testObj = buildTestObj()

            coEvery { samplerMock.sample(message) } returns sampledMessage
            coEvery { serializerMock.invoke(sampledMessage) } returns serialized
            coEvery { senderMock.send(serialized) } returns Unit

            testObj.handleMessage(message)

            coVerify { senderMock.send(serialized) }
        }
    }

    @Test
    fun `handleMessage - not sampled`() {
        runBlocking {
            val message = randMessage()
            val testObj = buildTestObj()

            coEvery { samplerMock.sample(message) } returns null
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
            val sampledMessage = randMessage()
            val testObj = buildTestObj()
            val exception = IOException()

            coEvery { samplerMock.sample(message) } throws exception
            coEvery { serializerMock.invoke(sampledMessage) } returns serialized
            coEvery { senderMock.send(serialized) } returns Unit

            testObj.handleMessage(message)

            coVerify(exactly = 0) { senderMock.send(serialized) }
            coVerify { exceptionHandlerMock.invoke(exception) }
        }
    }


    private fun buildTestObj(
        globalSampleRate: Double? = null,
        globalTags: Map<String, String?> = emptyMap()
    ): StatsD4KClient {
        return StatsD4KClient(
            serialize = serializerMock,
            sender = senderMock,
            sampler = samplerMock,
            globalSampleRate = globalSampleRate,
            globalTags = globalTags,
            exceptionHandler = exceptionHandlerMock
        )
    }
}