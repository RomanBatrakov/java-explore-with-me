package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.model.State;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/events")
@AllArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEventsByFilter(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size
    ) {
        log.info("GET request path /admin/events");
        return ResponseEntity.ok(eventService.getAllEventsByFilter(users, states, categories, rangeStart, rangeEnd,
                PageRequest.of(from, size)));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEventByAdmin(@PathVariable("eventId") Long id,
                                                       @RequestBody EventDto eventDto) {
        log.info("PUT request for path /admin/events/{eventId} with eventId={}", id);
        return ResponseEntity.ok(eventService.updateEventByAdmin(id, eventDto));
    }

    @PatchMapping("/{eventId}/publish")
    public ResponseEntity<EventDto> publishEvent(@PathVariable("eventId") Long id) {
        log.info("PATCH request for path /admin/events/{eventId}/publish with eventId={}", id);
        return ResponseEntity.ok(eventService.publishEvent(id));
    }
    @PatchMapping("/{eventId}/reject")
    public ResponseEntity<EventDto> rejectEvent(@PathVariable("eventId") Long id) {
        log.info("PATCH request for path /admin/events/{eventId}/reject with eventId={}", id);
        return ResponseEntity.ok(eventService.rejectEvent(id));
    }
}
