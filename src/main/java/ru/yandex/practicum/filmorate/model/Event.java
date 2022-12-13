package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Event {
    private Long eventId;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;
    private Instant timestamp;

    @JsonGetter("timestamp")
    public long timestampToEpochMilli() {
        return timestamp.toEpochMilli();
    }

}
