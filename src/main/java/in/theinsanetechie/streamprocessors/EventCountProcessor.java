package in.theinsanetechie.streamprocessors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Map;

public class EventCountProcessor {

    private static final String TIMESERIES_TOPIC = "wikimedia.stats.timeseries";
    private static final String TIMESERIES_STORE = "event-count-store";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final KStream<String, String> kafkaInputStream;

    public EventCountProcessor(KStream<String, String> kafkaInputStream) {
        this.kafkaInputStream = kafkaInputStream;
    }

    public void setup() {
        final TimeWindows timeWindows = TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10));
        this.kafkaInputStream
                .selectKey((key, value) -> "key-to-group")
                .groupByKey()
                .windowedBy(timeWindows)
                .count(Materialized.as(TIMESERIES_STORE))
                .toStream()
                .mapValues((readOnlyKey, value) -> {
                    final Map<String, Object> keyValueMap = Map.of(
                            "start_time", readOnlyKey.window().startTime().toString(),
                            "end_time", readOnlyKey.window().endTime().toString(),
                            "window_size", timeWindows.size(),
                            "event_count", value
                    );
                    try {
                        return OBJECT_MAPPER.writeValueAsString(keyValueMap);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .to(TIMESERIES_TOPIC, Produced.with(
                        WindowedSerdes.timeWindowedSerdeFrom(String.class, timeWindows.size()),
                        Serdes.String()
                ));
    }
}
