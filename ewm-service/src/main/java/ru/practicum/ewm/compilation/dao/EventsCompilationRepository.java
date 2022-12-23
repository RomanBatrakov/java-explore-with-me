package ru.practicum.ewm.compilation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;
@Repository
public interface EventsCompilationRepository extends JpaRepository<Event, Compilation> {
//List<Event> findAllByCompilation()
}
