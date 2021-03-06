# StatsD4K

![Build Status](https://github.com/lgwillmore/statsd4k/actions/workflows/test.yml/badge.svg?branch=main) ![version](https://img.shields.io/github/v/tag/lgwillmore/statsd4k?include_prereleases&label=version)

A StatsD client for kotlin. Built to be composable and allow for operation with various StatsD backends/servers.

## Composability

The `StatsD4KClient` provides composable components for customization of Serialization and Sending of messages.

## Tag Support
The client interface also provides `Tag` parameters for use with backends such as `New Relic` and `Datadog` (as long as
the serializers and senders are compatible)

## Gradle

```kotlin
repositories {
        maven(url = "https://laurencecodes.jfrog.io/artifactory/codes.laurence.statsd4k/")
}

dependencies {
    implementation("codes.laurence.statsd4k:statsd4k:2.0.0")
}
```

## New Relic Example

This assumes that you are using the local [New Relic statsd server](https://docs.newrelic.com/docs/integrations/host-integrations/host-integrations-list/statsd-monitoring-integration-version-2/#install).

```kotlin
val myStatsD4K = statsD4K {
    newRelic()
    udp()
}
myStatsD4K.count(
    bucket = "test.metric",
    value = 1,
    sampleRate = 0.5,
    tags = mapOf(
        "simple" to null,
        "key" to "value"
    )
)
```

## Null client

There is a `StatsD4kNull` implementation to drop in for local or when your statsd is not live or needed yet.


