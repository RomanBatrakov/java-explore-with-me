package ru.practicum.stats.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dao.HitRepository;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.Stats;

import javax.transaction.Transactional;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.stats.util.QPredicate.createPredicate;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Override
    public HitDto createHit(HitDto hitDto) {
        log.info("Creating hit");
        Hit hit = hitRepository.save(hitMapper.toHit(hitDto));
        return hitMapper.toHitDto(hit);
    }

    @Override
    public List<Stats> getStats(List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end) {
        log.info("Getting stats with filters");
        List<String> urisList = new ArrayList<>();
        for (String u : uris) {
            urisList.add(URLDecoder.decode(u, StandardCharsets.UTF_8));
        }
        Predicate predicate = createPredicate(urisList, start, end);
        List<Hit> hits = (List<Hit>) hitRepository.findAll(predicate);
        if (unique) hits = hits.stream()
                .sorted(Comparator.comparing(Hit::getIp))
                .distinct()
                .collect(Collectors.toList());
        List<Stats> statsList = HitMapper.hitsToStats(hits);
        if (statsList.isEmpty()) return List.of(new Stats("unavailable", "unavailable", 0));
        return statsList;
    }
}