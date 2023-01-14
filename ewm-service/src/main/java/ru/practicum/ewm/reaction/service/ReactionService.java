package ru.practicum.ewm.reaction.service;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.reaction.model.ReactionType;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface ReactionService {
    void createReaction(User user, Event event, ReactionType reactionType);

    void deleteById(User user, Event event);

    void setRating(List<Event> events);
}