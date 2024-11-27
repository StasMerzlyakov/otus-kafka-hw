package ru.otus.kafka.hw4.util;

import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Event {
    String key;
    String value;
}
