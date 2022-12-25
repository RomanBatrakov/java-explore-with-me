package ru.practicum.ewm.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<EventShortDto> getAllPublicEvents(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, PageRequest pageRequest, HttpServletRequest request) {
        return null;
    }

    @Override
    public EventDto getPublicEventById(Long id, HttpServletRequest request) {
        return null;
    }

    @Override
    public EventDto getEventById(Long eventId) {
        return null;
    }
    @Override
    public List<Event> eventsIdsToEvents(List<Long> events) {
        return eventRepository.findEventsByIdIn(events);
    }

    @Override
    public List<EventDto> getAllEventsByFilter(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest of) {
        return null;
    }

    @Override
    public EventDto updateEventByAdmin(Long id, EventDto eventDto) {
        return null;
    }

    @Override
    public EventDto publishEvent(Long id) {
        return null;
    }

    @Override
    public EventDto rejectEvent(Long id) {
        return null;
    }

    @Override
    public List<EventShortDto> getEventsByUser(Long id, PageRequest of) {
        return null;
    }

    @Override
    public EventDto updateEventByUser(Long id, UpdateEventDto updateEventDto) {
        return null;
    }

    @Override
    public EventDto createEventByUser(NewEventDto newEventDto, Long id) {
        return null;
    }
}
