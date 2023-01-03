package ru.practicum.stats.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping
@AllArgsConstructor
public class HitController {
    private final HitService hitService;

    @PostMapping("/hit")
    public ResponseEntity<HitDto> createEventByUser(@Valid @RequestBody HitDto hitDto) {
        log.info("POST request for path /hit with hitDto={}", hitDto);
        return ResponseEntity.ok(hitService.createHit(hitDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<Stats>> getStats(
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end
    ) {
        log.info("GET request path /stats");
        return ResponseEntity.ok(hitService.getStats(uris, unique, start, end));
    }
}