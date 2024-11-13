package ru.otus.kafka.hw4;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AdminUtils {

    public static final String HOST = "localhost:9091";

    public static final Map<String, Object> adminConfig = Map.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);

    public static Map<String, Object> createAdminConfig(Consumer<Map<String, Object>> builder) {
        var map = new HashMap<>(adminConfig);
        builder.accept(map);
        return map;
    }

    @FunctionalInterface
    public interface AdminAction {
        void doAction(Admin client) throws Exception;
    }

    public static void doAdminAction(Consumer<Map<String, Object>> adminConfigBuilder, AdminAction action) {
        try (var admin = Admin.create(createAdminConfig(adminConfigBuilder))) {
            action.doAction(admin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void recreateTopic(String name, int numPartitions, short replicationFactor, Map<String, Object> config) throws Exception {
        doAdminAction(confMap -> confMap.putAll(config), admin -> {
            DeleteTopicsResult deleteTopicsResult = admin.deleteTopics(List.of(name));
            while(deleteTopicsResult.all().isDone()) {
                Thread.sleep(500);
            }

            CreateTopicsResult createTopicsResult = admin.createTopics(List.of(new NewTopic(name, numPartitions, replicationFactor)));
            while(createTopicsResult.all().isDone()) {
                Thread.sleep(500);
            }
        });
    }
}
