package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.AdminUpdateEventDto;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private static final String EVENT_ID_PATH_VARIABLE_KEY = "eventId";
    private final EventService eventService;

    @GetMapping
    public List<EventDto> getAllEventsByFilter(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size
    ) {
        log.info("GET request path /admin/events");
        return eventService.getAllEventsByFilter(users, states, categories, rangeStart, rangeEnd,
                PageRequest.of(from, size));
    }

    @PutMapping("/{eventId}")
    public EventDto updateEventByAdmin(@PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long eventId,
                                       @RequestBody AdminUpdateEventDto adminUpdateEventDto) {
        log.info("PUT request for path /admin/events/{eventId} with eventId={}", eventId);
        return eventService.updateEventByAdmin(eventId, adminUpdateEventDto);
    }

    @PatchMapping("/{eventId}/publish")
    public EventDto publishEvent(@PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long id) {
        log.info("PATCH request for path /admin/events/{eventId}/publish with eventId={}", id);
        return eventService.publishEvent(id);
    }

    @PatchMapping("/{eventId}/reject")
    public EventDto rejectEvent(@PathVariable(EVENT_ID_PATH_VARIABLE_KEY) Long id) {
        log.info("PATCH request for path /admin/events/{eventId}/reject with eventId={}", id);
        return eventService.rejectEvent(id);
    }
}
