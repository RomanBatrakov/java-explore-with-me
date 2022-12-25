package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class PublicEventController {
    private final String SORT_VALIDATION = "^VIEWS$|^EVENT_DATE$";
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllPublicEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Long[] categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false)
            @Pattern(regexp = SORT_VALIDATION, message = "Incorrect type of sort") String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request) {
        log.info("GET request for path /events");
        return ResponseEntity.ok(eventService.getAllPublicEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, PageRequest.of(from, size), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getPublicEventById(@PathVariable("id") Long id, HttpServletRequest request) {
        log.info("GET request for path /events/{id} with id={}", id);
        return ResponseEntity.ok(eventService.getPublicEventById(id, request));
    }
}
