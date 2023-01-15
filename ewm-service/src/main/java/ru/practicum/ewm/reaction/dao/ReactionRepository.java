package ru.practicum.ewm.reaction.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.reaction.model.Reaction;
import ru.practicum.ewm.reaction.model.ReactionId;

import java.util.List;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, ReactionId> {
    List<Reaction> findAllById_EventIn(List<Event> events);
}