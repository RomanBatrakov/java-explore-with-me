package ru.practicum.ewm.event.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findEventsByIdIn(List<Long> longs);

    Event findEventByIdAndState(Long eventId, State state);

    Event findEventByIdAndInitiator_Id(Long eventId, Long userId);

    List<Event> findEventsByInitiator_Id(Long userId, Pageable pageable);

    Boolean existsEventsByCategory_Id(Long id);
}
