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
    private final EventService eventService;
    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsByUser(@PathVariable("userId") Long id,
                                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                               @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("GET request for path /users/{userId}/events with userId={}", id);
        return ResponseEntity.ok(eventService.getEventsByUser(id, PageRequest.of(from, size)));
    }

    @PatchMapping
    public ResponseEntity<EventDto> updateEventByUser(@PathVariable("userId") Long id,
                                    @Valid @RequestBody UpdateEventDto updateEventDto) {
        log.info("PATCH request for path /users/{userId}/events with userId={}", id);
        return ResponseEntity.ok(eventService.updateEventByUser(id, updateEventDto));
    }

    @PostMapping
    public ResponseEntity<EventDto> createEventByUser(@PathVariable("userId") Long id,
                                       @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST request POST for path /users/{userId}/events with userId={}", id);
        return ResponseEntity.ok(eventService.createEventByUser(newEventDto, id));
    }

}
