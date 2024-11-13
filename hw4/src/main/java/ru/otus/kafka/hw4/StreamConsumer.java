package ru.otus.kafka.hw4;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class StreamConsumer {

    public static String BOOTSTRAP = "localhost:9092";
    public static String TOPIC = "events";

    private static Logger logger = LoggerFactory.getLogger(StreamConsumer.class);

    public static void main(String[] args) throws Exception {
        AdminUtils.recreateTopic(TOPIC, 1, (short) 1,
                Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP));
        logger.info("topic {} created", TOPIC);

        var builder = new StreamsBuilder();
        Serde<String> stringSerde = Serdes.String();
        KStream<String, String> source = builder.stream(TOPIC, Consumed.with(stringSerde, stringSerde));
        // source.to("result", Produced.with(stringSerde, stringSerde));

        KStream<String, StreamUtils.Event> events = source.map((k, v) ->
                new KeyValue<>(k,
                new StreamUtils.Event.EventBuilder()
                        .key(k)
                        .value(v)
                        .build()));

        var eventSerde = StreamUtils.eventSerde();
        events.to("result", Produced.with(stringSerde, eventSerde));

        logger.info("{}", builder.build().describe());

        Properties streamProps = new Properties();
        streamProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        streamProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "hw4" + UUID.randomUUID());
        streamProps.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);

        //b.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);

        try (var kafkaStreams = new KafkaStreams(builder.build(), streamProps)) {
            kafkaStreams.start();
            while (!Thread.interrupted()) {
                Thread.sleep(2000);
            }
        }
/*


        Properties streamProps = new Properties();
        streamProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        streamProps.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group" + "-" + UUID.randomUUID());
        streamProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        streamProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer(streamProps)) {
            consumer.subscribe(Arrays.asList(TOPIC));
            while (!Thread.interrupted()) {
                var read = consumer.poll(Duration.ofSeconds(1));
                for (var record : read) {
                    logger.info("Receive {}:{} at {} - time {}",
                            record.key(), record.value(),
                            record.offset(),
                            record.timestamp()
                    );
                }
            }
        }*/
        //var serge = Serge<>

    }
}
