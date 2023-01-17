package ru.practicum.ewm.reaction.model;

import java.util.Optional;

public enum ReactionType {
    LIKE,
    DISLIKE;

    public static Optional<ReactionType> from(String reaction) {
        for (ReactionType type : values()) {
            if (type.name().equalsIgnoreCase(reaction)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}