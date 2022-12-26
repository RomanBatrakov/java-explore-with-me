package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users/{userId}/events")
@AllArgsConstructor
public class PrivateEventController {
    private static final String EVENT_ID_PATH_VARIABLE_KEY = "eventId";
    private static final String USER_ID_PATH_VARIABLE_KEY = "userId";
    private static final String REQUEST_ID_PATH_VARIABLE_KEY = "reqId";

    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsByUser(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long id,
                                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                               @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("GET request for path /users/{userId}/events with userId={}", id);
        return ResponseEntity.ok(eventService.getEventsByUser(id, PageRequest.of(from, size)));
    }

    @PatchMapping
    public ResponseEntity<EventDto> updateEventByUser(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long id,
                                                      @Valid @RequestBody UpdateEventDto updateEventDto) {
        log.info("PATCH request for path /users/{userId}/events with userId={}", id);
        return ResponseEntity.ok(eventService.updateEventByUser(id, updateEventDto));
    }

    @PostMapping
    public ResponseEntity<EventDto> createEventByUser(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long id,
                                                      @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST request POST for path /users/{userId}/events with userId={}", id);
        return ResponseEntity.ok(eventService.createEventByUser(newEventDto, id));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getUserEvent(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long id,
                                                 @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId) {
        log.info("GET request for path /users/{userId}/events/{eventId} with userId={}, eventId={}", id, eventId);
        return ResponseEntity.ok(eventService.getUserEvent(id, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDto> cancelEventByUser(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long id,
                                                      @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId) {
        log.info("PATCH request for path /users/{userId}/events/{eventId} with userId={}, eventId={}", id, eventId);
        return ResponseEntity.ok(eventService.cancelEventByUser(id, eventId));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<RequestDto>> getUserRequests(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long id,
                                                            @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId) {
        log.info("GET request for path /users/{userId}/events/{eventId}/requests with userId={}, eventId={}",
                id, eventId);
        return ResponseEntity.ok(requestService.getUserRequests(id, eventId));
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ResponseEntity<RequestDto> confirmRequest(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long id,
                                                     @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId,
                                                     @PathVariable(REQUEST_ID_PATH_VARIABLE_KEY) Long requestId) {
        log.info("PATCH request for path /users/{userId}/events/{eventId}/requests/{reqId}/confirm with userId={}," +
                " eventId={}, reqId={}", id, eventId, requestId);
        return ResponseEntity.ok(requestService.confirmRequest(id, eventId, requestId));
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ResponseEntity<RequestDto> rejectRequest(@PathVariable(USER_ID_PATH_VARIABLE_KEY) Long id,
                                                    @PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId,
                                                    @PathVariable(REQUEST_ID_PATH_VARIABLE_KEY) Long requestId) {
        log.info("PATCH request for path /users/{userId}/events/{eventId}/requests/{reqId}/reject with userId={}," +
                " eventId={}, reqId={}", id, eventId, requestId);
        return ResponseEntity.ok(requestService.rejectRequest(id, eventId, requestId));
    }

}
