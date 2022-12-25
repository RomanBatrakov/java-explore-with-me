package ru.practicum.ewm.event.model;

import java.util.Optional;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED;
//TODO: удалить если не пригодиться
    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
