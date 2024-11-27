package ru.otus.kafka.hw4.util;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class StreamUtils {

    private static <T> Serde<T> serde(Class<T> cls) {
        return new Serdes.WrapperSerde<>(new JsonSerializer<>(), new JsonDeserializer<>(cls));
    }

    public static Serde<Event> eventSerde() {
        return serde(Event.class);
    }

    public static Serde<CompoundEvent> compoundEventSerge() { return serde(CompoundEvent.class); }

}
