package ru.otus.kafka.hw4;

import lombok.Builder;
import lombok.Value;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class StreamUtils {

    @Value
    @Builder(toBuilder = true)
    static class Event {
        String key;
        String value;
    }

    private static <T> Serde<T> serde(Class<T> cls) {
        return new Serdes.WrapperSerde<>(new JsonSerializer<>(), new JsonDeserializer<>(cls));
    }

    public static Serde<Event> eventSerde() {
        return serde(Event.class);
    }
}
