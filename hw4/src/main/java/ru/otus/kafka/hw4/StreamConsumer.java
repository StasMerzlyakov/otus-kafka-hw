package ru.otus.kafka.hw4;

import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Map;

public class StreamConsumer {
    public static void main(String[] args) throws Exception {
        AdminUtils.recreateTopic("event", 1, (short) 1,
                Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"));



    }
}
