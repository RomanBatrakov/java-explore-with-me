package ru.practicum.ewm.event.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.exeption.ValidationException;
import ru.practicum.ewm.hit.client.HitClient;
import ru.practicum.ewm.hit.model.Hit;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.practicum.ewm.util.QPredicates.createAdminPredicate;
import static ru.practicum.ewm.util.QPredicates.createPublicPredicate;

@Slf4j
@Service
@Transactional
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now().withNano(0);
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final RequestService requestService;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final HitClient hitClient;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, UserMapper userMapper,
                            UserService userService, @Lazy RequestService requestService,
                            @Lazy CategoryService categoryService, CategoryMapper categoryMapper, HitClient hitClient) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.userService = userService;
        this.requestService = requestService;
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.hitClient = hitClient;
    }

    @Override
    public List<EventShortDto> getAllPublicEvents(String text, Long[] categories, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable, String sort, Pageable pageable,
                                                  HttpServletRequest request) {
        log.info("Getting all public events with filters");
        postHitToStatsServer(request);
        Predicate predicate = createPublicPredicate(text, categories, paid, rangeStart, rangeEnd);
        List<Event> events = (predicate == null) ? eventRepository.findAll(pageable).getContent()
                : eventRepository.findAll(predicate, pageable).getContent();
        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> (e.getParticipantLimit() != 0L)
                            && (e.getParticipantLimit() > e.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }
        return eventMapper.toEventShortDtoList(events);
    }

    @Override
    public EventDto getPublicEventById(Long eventId, HttpServletRequest request) {
        try {
            log.info("Getting public event by id={}", eventId);
            postHitToStatsServer(request);
            Event event = eventRepository.findEventByIdAndState(eventId, State.PUBLISHED);
            event.setViews(event.getViews() + 1);
            return eventMapper.toEventDto(event);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Event with id %s is not found", eventId));
        }
    }

    @Override
    public EventDto getEventById(Long eventId) {
        try {
            log.info("Getting event by id={}", eventId);
            return eventMapper.toEventDto(eventRepository.findById(eventId).get());
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Event with id %s is not found", eventId));
        }
    }

    @Override
    public List<Event> eventsIdsToEvents(List<Long> events) {
        log.info("Converting events ids: {} to events", events);
        return eventRepository.findEventsByIdIn(events);
    }

    @Override
    public List<EventDto> getAllEventsByFilter(List<Long> users, List<State> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        log.info("Getting all events with filters: users: {}, states: {}, categories: {}, start: {}, end: {},",
                users, states, categories, rangeStart, rangeEnd);
        Predicate predicate = createAdminPredicate(users, states, categories, rangeStart, rangeEnd);
        List<Event> events = (predicate == null) ? eventRepository.findAll(pageable).getContent()
                : eventRepository.findAll(predicate, pageable).getContent();
        return eventMapper.toEventDtoList(events);
    }

    @Override
    public EventDto updateEventByAdmin(Long eventId, AdminUpdateEventDto adminUpdateEventDto) {
        log.info("Updating event by admin, eventId={}", eventId);
        Long catId = adminUpdateEventDto.getCategory();
        Event event = eventMapper.toEvent(getEventById(eventId));
        Event updatedEvent = eventMapper.updateEventByAdmin(adminUpdateEventDto, event);
        if (catId != null) {
            Category category = categoryMapper.toCategory(categoryService.getCategoryById(catId));
            updatedEvent.setCategory(category);
        }
        return eventMapper.toEventDto(eventRepository.save(updatedEvent));
    }

    @Override
    public EventDto publishEvent(Long eventId) {
        log.info("Publishing event by admin, eventId={}", eventId);
        Event event = eventMapper.toEvent(getEventById(eventId));
        if (event.getEventDate().isAfter(LOCAL_DATE_TIME_NOW.plusHours(1))
                && event.getState().equals(State.PENDING)) {
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LOCAL_DATE_TIME_NOW);
            return eventMapper.toEventDto(eventRepository.save(event));
        } else {
            throw new ValidationException("Can't publish this event");
        }
    }

    @Override
    public EventDto rejectEvent(Long eventId) {
        log.info("Canceling event by admin, eventId={}", eventId);
        Event event = eventMapper.toEvent(getEventById(eventId));
        if (!event.getState().equals(State.PUBLISHED)) {
            event.setState(State.CANCELED);
            return eventMapper.toEventDto(eventRepository.save(event));
        } else {
            throw new ValidationException("Can't cancel this event");
        }
    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Pageable pageable) {
        try {
            log.info("Getting events by userId={}", userId);
            List<Event> eventsList = eventRepository.findEventsByInitiator_Id(userId, pageable);
            return eventMapper.toEventShortDtoList(eventsList);
        } catch (NoSuchElementException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public EventDto updateEventByUser(Long userId, UpdateEventDto updateEventDto) {
        log.info("Updating event by user: userId={}", userId);
        Long eventId = updateEventDto.getEventId();
        Long catId = updateEventDto.getCategory();
        try {
            Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId);
            if (event.getState().equals(State.PUBLISHED)
                    || event.getEventDate().isBefore(LOCAL_DATE_TIME_NOW.plusHours(2)))
                throw new ValidationException("Wrong state or eventDate");
            if (event.getState().equals(State.CANCELED)) event.setState(State.PENDING);
            Event updatedEvent = eventMapper.updateEventByUser(updateEventDto, event);
            if (catId != null) {
                Category category = categoryMapper.toCategory(categoryService.getCategoryById(catId));
                updatedEvent.setCategory(category);
            }
            return eventMapper.toEventDto(eventRepository.save(updatedEvent));
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Event with id %s is not found", eventId));
        }
    }

    @Override
    public EventDto createEventByUser(NewEventDto newEventDto, Long userId) {
        log.info("Creating event by user: userId={}", userId);
        User user = userMapper.toUser(userService.getUserById(userId));
        Event event = eventMapper.fromNewEvent(newEventDto);
        Category category = categoryMapper.toCategory(categoryService.getCategoryById(newEventDto.getCategory()));
        if (event.getEventDate().isBefore(LOCAL_DATE_TIME_NOW.plusHours(2)))
            throw new ValidationException("Wrong eventDate");
        event.setInitiator(user);
        event.setCategory(category);
        event.setCreatedOn(LOCAL_DATE_TIME_NOW);
        event.setConfirmedRequests(0L);
        event.setViews(0L);
        event.setState(State.PENDING);
        return eventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    public EventDto getUserEvent(Long userId, Long eventId) {
        try {
            log.info("Getting user event by eventId={}, userId={}", eventId, userId);
            Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId);
            return eventMapper.toEventDto(event);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Event with id %s is not found", eventId));
        }
    }

    @Override
    public EventDto cancelEventByUser(Long userId, Long eventId) {
        try {
            log.info("Canceling event by user: eventId={}, userId={}", eventId, userId);
            Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId);
            if (event.getState().equals(State.PENDING)) {
                event.setState(State.CANCELED);
                return eventMapper.toEventDto(eventRepository.save(event));
            } else {
                throw new ValidationException("Wrong event state");
            }
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Event with id %s is not found", eventId));
        }
    }

    @Override
    public void addParticipant(Event event) {
        log.info("Add participant to event={}", event);
        Long approved = event.getConfirmedRequests();
        event.setConfirmedRequests(++approved);
        eventRepository.save(event);
        if (event.getParticipantLimit() <= approved) requestService.rejectOtherRequests(event.getId());
    }

    @Override
    public void deleteParticipant(Long eventId) {
        log.info("Delete participant from event, eventId={}", eventId);
        Event event = eventMapper.toEvent(getEventById(eventId));
        Long approved = event.getConfirmedRequests();
        event.setConfirmedRequests(--approved);
        eventRepository.save(event);
    }

    private void postHitToStatsServer(HttpServletRequest request) {
        try {
            hitClient.postHit(
                    new Hit(request.getServerName(), request.getRequestURI(), request.getRemoteAddr(),
                            LocalDateTime.now().format(DATE_TIME_FORMATTER))
            );
        } catch (WebClientRequestException e) {
            log.warn("Hit not saved because: {}", e.getCause().getMessage());
        }
    }
}