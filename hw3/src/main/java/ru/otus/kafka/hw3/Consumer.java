package ru.otus.kafka.hw3;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

public class Consumer {

    private static Logger logger = LoggerFactory.getLogger(Consumer.class);

    public static void main(String[] args) throws Exception {

        Properties transactionConsumerProps = new Properties();
        transactionConsumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        transactionConsumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group" + "-" + UUID.randomUUID());
        transactionConsumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        transactionConsumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        transactionConsumerProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        transactionConsumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer(transactionConsumerProps)) {
            consumer.subscribe(Arrays.asList("topic1", "topic2"));
            while (!Thread.interrupted()) {
                var read = consumer.poll(Duration.ofSeconds(1));
                for (var record : read) {
                    logger.info("Receive {}:{} at {}", record.key(), record.value(), record.offset());
                }
            }
        }


    }
}
