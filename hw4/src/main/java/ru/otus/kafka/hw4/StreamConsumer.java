package ru.otus.kafka.hw4;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class StreamConsumer {

    public static String BOOTSTRAP = "localhost:9092";


    private static Logger logger = LoggerFactory.getLogger(StreamConsumer.class);

    public static void main(String[] args) throws Exception {
        AdminUtils.recreateTopic("event", 1, (short) 1,
                Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP));
        logger.info("topic event created");

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group" + "-" + UUID.randomUUID());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer(consumerProps)) {
            consumer.subscribe(Arrays.asList("event"));
            while (!Thread.interrupted()) {
                var read = consumer.poll(Duration.ofSeconds(1));
                for (var record : read) {
                    logger.info("Receive {}:{} at {}", record.key(), record.value(), record.offset());
                }
            }
        }

    }
}
