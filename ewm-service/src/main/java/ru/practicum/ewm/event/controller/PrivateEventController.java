package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private static final String EVENT_ID_PATH_VARIABLE_KEY = "eventId";
    private static final String USER_ID_PATH_VARIABLE_KEY = "userId";
    private static final String REQUEST_ID_PATH_VARIABLE_KEY = "reqId";
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long userId,
                                               @RequestParam(required = false, defaultValue = "0")
                                               @PositiveOrZero int from,
                                               @RequestParam(required = false, defaultValue = "10")
                                               @Positive int size) {
        log.info("GET request for path /users/{userId}/events with userId={}", userId);
        return eventService.getEventsByUser(userId, PageRequest.of(from, size));
    }

    @PatchMapping
    public EventDto updateEventByUser(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long userId,
                                      @Valid @RequestBody UpdateEventDto updateEventDto) {
        log.info("PATCH request for path /users/{userId}/events with userId={}", userId);
        return eventService.updateEventByUser(userId, updateEventDto);
    }

    @PostMapping
    public EventDto createEventByUser(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long userId,
                                      @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST request for path /users/{userId}/events with userId={}", userId);
        return eventService.createEventByUser(newEventDto, userId);
    }

    @GetMapping("/{eventId}")
    public EventDto getUserEvent(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long userId,
                                 @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId) {
        log.info("GET request for path /users/{userId}/events/{eventId} with userId={}, eventId={}", userId, eventId);
        return eventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto cancelEventByUser(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long userId,
                                      @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId) {
        log.info("PATCH request for path /users/{userId}/events/{eventId} with userId={}, eventId={}", userId, eventId);
        return eventService.cancelEventByUser(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getUserRequests(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long userId,
                                            @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId) {
        log.info("GET request for path /users/{userId}/events/{eventId}/requests with userId={}, eventId={}",
                userId, eventId);
        return requestService.getUserRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequest(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long userId,
                                     @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId,
                                     @PathVariable(REQUEST_ID_PATH_VARIABLE_KEY) Long requestId) {
        log.info("PATCH request for path /users/{userId}/events/{eventId}/requests/{reqId}/confirm with userId={}," +
                " eventId={}, reqId={}", userId, eventId, requestId);
        return requestService
                .changeRequestStatus(userId, eventId, requestId, RequestStatus.CONFIRMED);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequest(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long userId,
                                    @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId,
                                    @PathVariable(REQUEST_ID_PATH_VARIABLE_KEY) Long requestId) {
        log.info("PATCH request for path /users/{userId}/events/{eventId}/requests/{reqId}/reject with userId={}," +
                " eventId={}, reqId={}", userId, eventId, requestId);
        return requestService
                .changeRequestStatus(userId, eventId, requestId, RequestStatus.REJECTED);
    }

    @PostMapping("/{eventId}/{reaction}")
    public void createReaction(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long userId,
                               @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId,
                               @PathVariable(name = "reaction") String reaction) {
        log.info("POST request for path /users/{userId}/events/{eventId}/{reaction} with userId={}, eventId={}," +
                " reaction={}", userId, eventId, reaction);
        eventService.createReaction(userId, eventId, reaction);
    }
}