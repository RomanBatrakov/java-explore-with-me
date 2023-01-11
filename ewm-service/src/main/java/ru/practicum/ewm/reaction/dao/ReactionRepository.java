package ru.practicum.ewm.reaction.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.reaction.model.Reaction;
import ru.practicum.ewm.reaction.model.ReactionId;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, ReactionId> {
}