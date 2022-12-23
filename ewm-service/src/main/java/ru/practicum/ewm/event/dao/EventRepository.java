package ru.practicum.ewm.event.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}