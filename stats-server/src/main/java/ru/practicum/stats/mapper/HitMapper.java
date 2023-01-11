package ru.practicum.stats.mapper;

import com.mysema.commons.lang.Pair;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.Stats;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface HitMapper {
    Hit toHit(HitDto hitDto);

    HitDto toHitDto(Hit hit);

    static List<Stats> hitsToStats(List<Hit> hits) {
        return hits.stream()
                .collect(Collectors.groupingBy(
                        hit -> new Pair<>(hit.getApp(), hit.getUri()),
                        Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new Stats(
                        entry.getKey().getFirst(),
                        entry.getKey().getSecond(),
                        entry.getValue().intValue()))
                .collect(Collectors.toList());
    }
}