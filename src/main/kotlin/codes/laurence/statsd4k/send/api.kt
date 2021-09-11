package codes.laurence.statsd4k.send

interface StatsDSender {

    suspend fun send(message: String)

}