package ru.otus.kafka.hw4;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kafka.hw4.util.AdminUtils;
import ru.otus.kafka.hw4.util.CompoundEvent;
import ru.otus.kafka.hw4.util.Event;
import ru.otus.kafka.hw4.util.StreamUtils;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class StreamConsumer {

    public static String BOOTSTRAP = "localhost:9092";
    public static String TOPIC = "events";

    private static final Logger logger = LoggerFactory.getLogger(StreamConsumer.class);

    public static void main(String[] args) throws Exception {
        AdminUtils.recreateTopic(TOPIC, 3, (short) 1,
                Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP));
        logger.info("topic {} created", TOPIC);

        var builder = new StreamsBuilder();

        // Преобразуем поток входных данные в Event

        var stringSerde = Serdes.String();
        var eventSerde = StreamUtils.eventSerde();
        var compoundEventSerge = StreamUtils.compoundEventSerge();
        var longSerde = Serdes.Long();

        KStream<String, Event> eventSource = builder.stream(TOPIC,
                Consumed.with(stringSerde,
                        eventSerde));

        // делаем map на CompoundEvent
        var compoundEventSource = eventSource.map((k, v) ->
                KeyValue.pair(v.getKey(), CompoundEvent.builder().key(v.getKey()).count(1L).build()));

        // Выбираем ключ партиции на основе значения поля event.key
        var keyStream = compoundEventSource.selectKey((k, v) -> v.getKey());

        var window = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5));

        var resultStream = keyStream
                .groupByKey(Grouped.with(stringSerde, compoundEventSerge))
                .windowedBy(window)
                .reduce(CompoundEvent::merge, Materialized.as("stock-group"))
                .toStream()
                .map((w, compoundEvent) ->
                        KeyValue.pair(w.key(), compoundEvent))
                .filter((k, v) -> v != null);

        resultStream.to("result", Produced.with(stringSerde, compoundEventSerge));
        resultStream.peek((k, v) -> logger.info("Source {}: {}", k, v));

        logger.info("{}", builder.build().describe());

        Properties streamProps = new Properties();
        streamProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        streamProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "hw4");//UUID.randomUUID());
        streamProps.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        streamProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class); // **
        streamProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        streamProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (var kafkaStreams = new KafkaStreams(builder.build(), streamProps)) {
            kafkaStreams.setUncaughtExceptionHandler(th -> {
                th.printStackTrace();
                return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;
            });
            kafkaStreams.start();
            while (!Thread.interrupted()) {
                Thread.sleep(2000);
            }
        }
    }
}
