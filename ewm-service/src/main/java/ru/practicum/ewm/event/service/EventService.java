package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getAllPublicEvents(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                           PageRequest pageRequest, HttpServletRequest request);

    EventDto getPublicEventById(Long id, HttpServletRequest request);

    EventDto getEventById(Long eventId);

    List<Event> eventsIdsToEvents(List<Long> events);

    List<EventDto> getAllEventsByFilter(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart,
                                LocalDateTime rangeEnd, PageRequest of);

    EventDto updateEventByAdmin(Long id, EventDto eventDto);

    EventDto publishEvent(Long id);

    EventDto rejectEvent(Long id);

    List<EventShortDto> getEventsByUser(Long id, PageRequest of);

    EventDto updateEventByUser(Long id, UpdateEventDto updateEventDto);

    EventDto createEventByUser(NewEventDto newEventDto, Long id);

    EventDto getUserEvent(Long id, Long eventId);

    EventDto cancelEventByUser(Long id, Long eventId);
}
