# StatsD4K

A StatsD client for kotlin. Built to be composable and allow for operation with various StatsD backends/servers.

> This project is currently a work in progress, and will be targeting a New Relic backend initially

## Composability

The `StatsD4KClient` provides composable components for customization of:

- Serialization with the `StatsDSerializer` interface.
- Sending with the `StatsDSender` interface.

## Tag Support
The client interface also provides `Tag` parameters for use with backends such as `New Relic` and `Datadog` (as long as
the serializers and senders are compatible)


