package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.reaction.dto.ReactionDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getAllPublicEvents(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, Boolean popular, String sort,
                                           Pageable pageable, HttpServletRequest request);

    EventDto getPublicEventById(Long eventId, HttpServletRequest request);

    EventDto getEventById(Long eventId);

    List<Event> eventsIdsToEvents(List<Long> events);

    List<EventDto> getAllEventsByFilter(List<Long> users, List<State> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    EventDto updateEventByAdmin(Long id, AdminUpdateEventDto eventDto);

    EventDto publishEvent(Long eventId);

    EventDto rejectEvent(Long eventId);

    List<EventShortDto> getEventsByUser(Long userId, Pageable pageable);

    List<Event> getEventListByUser(Long userId, Pageable pageable);

    EventDto updateEventByUser(Long userId, UpdateEventDto updateEventDto);

    EventDto createEventByUser(NewEventDto newEventDto, Long userId);

    EventDto getUserEvent(Long userId, Long eventId);

    EventDto cancelEventByUser(Long userId, Long eventId);

    void checkParticipantLimit(Event event);

    void addViewsToEvents(List<Event> events);

    Boolean existsByCategoryId(Long id);

    ReactionDto createReaction(Long userId, Long eventId, String reaction);

    void deleteReaction(Long userId, Long eventId);

    Event findPublishedEventById(Long eventId);
}
