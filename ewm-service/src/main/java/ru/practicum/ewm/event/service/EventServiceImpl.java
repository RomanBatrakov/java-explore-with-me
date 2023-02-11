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
import ru.practicum.ewm.hit.model.Stats;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.reaction.dto.ReactionDto;
import ru.practicum.ewm.reaction.model.ReactionType;
import ru.practicum.ewm.reaction.service.ReactionService;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.model.State.PUBLISHED;
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
    private final ReactionService reactionService;
    private final HitClient hitClient;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, UserMapper userMapper,
                            @Lazy UserService userService, @Lazy RequestService requestService,
                            @Lazy CategoryService categoryService, CategoryMapper categoryMapper,
                            ReactionService reactionService, HitClient hitClient) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.userService = userService;
        this.requestService = requestService;
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.reactionService = reactionService;
        this.hitClient = hitClient;
    }

    @Override
    public List<EventShortDto> getAllPublicEvents(String text, Long[] categories, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable, Boolean popular, String sort,
                                                  Pageable pageable, HttpServletRequest request) {
        log.info("Getting all public events with filters");
        Predicate predicate = createPublicPredicate(text, categories, paid, rangeStart, rangeEnd);
        List<Event> events = (predicate == null) ? eventRepository.findAll(pageable).getContent()
                : eventRepository.findAll(predicate, pageable).getContent();
        requestService.setConfirmedRequestsFromDb(events);
        reactionService.setRating(events);
        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> (e.getParticipantLimit() != 0L)
                            && (e.getParticipantLimit() > e.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }
        if (popular) {
            events = events.stream()
                    .sorted(Comparator.comparing(e -> e.getRating().getRating(), Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }
        postHitToStatsServer(request);
        addViewsToEvents(events);
        return eventMapper.toEventShortDtoList(events);
    }

    @Override
    public EventDto getPublicEventById(Long eventId, HttpServletRequest request) {
        try {
            log.info("Getting public event by id={}", eventId);
            Event event = eventRepository.findEventByIdAndState(eventId, PUBLISHED);
            requestService.setConfirmedRequestsFromDb(Collections.singletonList(event));
            reactionService.setRating(Collections.singletonList(event));
            addViewsToEvents(Collections.singletonList(event));
            postHitToStatsServer(request);
            return eventMapper.toEventDto(event);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Event with id %s is not found", eventId));
        }
    }

    @Override
    public EventDto getEventById(Long eventId) {
        try {
            log.info("Getting event by id={}", eventId);
            Event event = eventRepository.findById(eventId).get();
            requestService.setConfirmedRequestsFromDb(Collections.singletonList(event));
            reactionService.setRating(Collections.singletonList(event));
            addViewsToEvents(Collections.singletonList(event));
            return eventMapper.toEventDto(event);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Event with id %s is not found", eventId));
        }
    }

    @Override
    public List<Event> eventsIdsToEvents(List<Long> events) {
        log.info("Converting events ids: {} to events", events);
        List<Event> eventList = eventRepository.findEventsByIdIn(events);
        requestService.setConfirmedRequestsFromDb(eventList);
        reactionService.setRating(eventList);
        addViewsToEvents(eventList);
        return eventList;
    }

    @Override
    public List<EventDto> getAllEventsByFilter(List<Long> users, List<State> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        log.info("Getting all events with filters: users: {}, states: {}, categories: {}, start: {}, end: {},",
                users, states, categories, rangeStart, rangeEnd);
        Predicate predicate = createAdminPredicate(users, states, categories, rangeStart, rangeEnd);
        List<Event> events = (predicate == null) ? eventRepository.findAll(pageable).getContent()
                : eventRepository.findAll(predicate, pageable).getContent();
        requestService.setConfirmedRequestsFromDb(events);
        reactionService.setRating(events);
        addViewsToEvents(events);
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
            event.setState(PUBLISHED);
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
        if (!event.getState().equals(PUBLISHED)) {
            event.setState(State.CANCELED);
            return eventMapper.toEventDto(eventRepository.save(event));
        } else {
            throw new ValidationException("Can't cancel this event");
        }
    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Pageable pageable) {
        log.info("Getting events by userId={}", userId);
        List<Event> eventList = getEventListByUser(userId, pageable);
        return eventMapper.toEventShortDtoList(eventList);
    }

    @Override
    public List<Event> getEventListByUser(Long userId, Pageable pageable) {
        try {
            log.info("Getting eventList by userId={}", userId);
            List<Event> eventList = eventRepository.findEventsByInitiator_Id(userId, pageable);
            requestService.setConfirmedRequestsFromDb(eventList);
            reactionService.setRating(eventList);
            addViewsToEvents(eventList);
            return eventList;
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
            if (event.getState().equals(PUBLISHED)
                    || event.getEventDate().isBefore(LOCAL_DATE_TIME_NOW.plusHours(2)))
                throw new ValidationException("Wrong state or eventDate");
            if (event.getState().equals(State.CANCELED)) event.setState(State.PENDING);
            Event updatedEvent = eventMapper.updateEventByUser(updateEventDto, event);
            if (catId != null) {
                Category category = categoryMapper.toCategory(categoryService.getCategoryById(catId));
                updatedEvent.setCategory(category);
            }
            requestService.setConfirmedRequestsFromDb(Collections.singletonList(updatedEvent));
            reactionService.setRating(Collections.singletonList(updatedEvent));
            addViewsToEvents(Collections.singletonList(updatedEvent));
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
        event.setRating(Rating.builder().build());
        return eventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    public EventDto getUserEvent(Long userId, Long eventId) {
        try {
            log.info("Getting user event by eventId={}, userId={}", eventId, userId);
            Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId);
            requestService.setConfirmedRequestsFromDb(Collections.singletonList(event));
            reactionService.setRating(Collections.singletonList(event));
            addViewsToEvents(Collections.singletonList(event));
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
                requestService.setConfirmedRequestsFromDb(Collections.singletonList(event));
                reactionService.setRating(Collections.singletonList(event));
                addViewsToEvents(Collections.singletonList(event));
                return eventMapper.toEventDto(eventRepository.save(event));
            } else {
                throw new ValidationException("Wrong event state");
            }
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Event with id %s is not found", eventId));
        }
    }

    @Override
    public void checkParticipantLimit(Event event) {
        log.info("Check participant to event={}", event);
        if (event.getParticipantLimit() <= event.getConfirmedRequests())
            requestService.rejectOtherRequests(event.getId());
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

    @Override
    public void addViewsToEvents(List<Event> events) {
        log.info("Adding views to events={}", events);
        List<String> eventsUrl = events.stream()
                .map(Event::getId)
                .map(id -> URLEncoder.encode("/events/" + id, StandardCharsets.UTF_8))
                .collect(Collectors.toList());
        List<Stats> statsList = hitClient.findStats(LOCAL_DATE_TIME_NOW.minusYears(3),
                LOCAL_DATE_TIME_NOW.plusYears(10), eventsUrl, false);
        Map<String, Long> statMap = statsList.stream()
                .collect(Collectors.toMap(Stats::getUri, Stats::getHits));
        events.forEach(event -> event.setViews(statMap.getOrDefault("/events/" + event.getId(), 0L)));
    }

    @Override
    public Boolean existsByCategoryId(Long id) {
        return eventRepository.existsEventsByCategory_Id(id);
    }

    @Override
    public ReactionDto createReaction(Long userId, Long eventId, String reaction) {
        log.info("Creating reaction={}", reaction);
        ReactionType reactionType = ReactionType.from(reaction)
                .orElseThrow(() -> new ValidationException("Unknown reaction: " + reaction));
        Event event = findPublishedEventById(eventId);
        User user = userMapper.toUser(userService.getUserById(userId));
        if (!event.getInitiator().equals(user) && requestService.validateUserParticipation(userId, eventId)) {
            return reactionService.createReaction(user, event, reactionType);
        } else {
            throw new ValidationException(String.format("User %s is not a event %s participant", userId, eventId));
        }
    }

    @Override
    public void deleteReaction(Long userId, Long eventId) {
        log.info("Deleting reaction with userId={}, eventId={}", userId, eventId);
        Event event = findPublishedEventById(eventId);
        User user = userMapper.toUser(userService.getUserById(userId));
        reactionService.deleteById(user, event);
    }

    @Override
    public Event findPublishedEventById(Long eventId) {
        try {
            log.info("Finding event={}", eventId);
            return eventRepository.findEventByIdAndState(eventId, PUBLISHED);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Event with id %s is not found", eventId));
        }
    }
}
