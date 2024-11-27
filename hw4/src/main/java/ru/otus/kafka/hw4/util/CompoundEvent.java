package ru.otus.kafka.hw4.util;

import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CompoundEvent {
    String key;
    Long count;

    public CompoundEvent merge(CompoundEvent next){
        this.count += next.count;
        return this;
    }
}


