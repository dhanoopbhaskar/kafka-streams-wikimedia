# kafka-streams-wikimedia

Related repository - [kafka-producer-wikimedia](https://github.com/dhanoopbhaskar/kafka-producer-wikimedia)

## Start Zookeeper
    zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties

if installed using brew,

    zookeeper-server-start /opt/homebrew/etc/kafka/zookeeper.properties

## Start Kafka server
    kafka-server-start /usr/local/etc/kafka/server.properties

if installed using brew,

    kafka-server-start /opt/homebrew/etc/kafka/server.properties

## Start kafka-producer-wikimedia

## Read from the topics

    kafka-console-consumer --bootstrap-server localhost:9092 --topic wikimedia.stats.bots

    kafka-console-consumer --bootstrap-server localhost:9092 --topic wikimedia.stats.website

    kafka-console-consumer --bootstrap-server localhost:9092 --topic wikimedia.stats.timeseries
