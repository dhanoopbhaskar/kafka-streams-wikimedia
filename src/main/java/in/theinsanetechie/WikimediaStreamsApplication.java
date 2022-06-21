package in.theinsanetechie;

import in.theinsanetechie.streamprocessors.BotCountProcessor;
import in.theinsanetechie.streamprocessors.EventCountProcessor;
import in.theinsanetechie.streamprocessors.WebsiteCountProcessor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class WikimediaStreamsApplication {

    private static final Logger log = LoggerFactory.getLogger(WikimediaStreamsApplication.class);
    private static final String INPUT_TOPIC = "wikimedia.recentChanges";

    public static void main(String[] args) {
        // properties
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "wikimedia-streams-application");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> kafkaStream = builder.stream(INPUT_TOPIC);

        BotCountProcessor botCountProcessor = new BotCountProcessor(kafkaStream);
        botCountProcessor.setup();

        WebsiteCountProcessor websiteCountProcessor = new WebsiteCountProcessor(kafkaStream);
        websiteCountProcessor.setup();

        EventCountProcessor eventCountProcessor = new EventCountProcessor(kafkaStream);
        eventCountProcessor.setup();

        final Topology appTopology = builder.build();
        log.info("Topology: {}", appTopology.describe());

        KafkaStreams streams = new KafkaStreams(appTopology, properties);
        streams.start();
    }
}
