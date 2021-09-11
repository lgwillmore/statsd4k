package codes.laurence.statsd4k.serialize

import codes.laurence.statsd4k.message.Message

typealias StatsDSerializer = (message: Message<*>) -> String