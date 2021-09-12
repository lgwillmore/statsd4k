package codes.laurence.statsd4k.serialize


/**
 * A Serializer for handling New Relic style tags.
 *
 * <metric name>:<value>|<type>|@<sample rate>|#<tags>
 */
val StatsDSerializerNewRelic: StatsDSerializer = { message ->
    val base = StatsDSerializerBase(message)
    if (message.tags.isEmpty()) {
        base
    } else {
        var tags = "|#"
        message.tags.forEach { (t, u) ->
            tags += if (u == null) {
                "$t,"
            } else {
                "$t:$u,"
            }
        }
        "$base$tags"
    }
}