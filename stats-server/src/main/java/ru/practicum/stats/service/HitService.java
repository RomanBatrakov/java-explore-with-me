package ru.practicum.stats.service;

import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    HitDto createHit(HitDto hitDto);

    List<Stats> getStats(List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end);
}
