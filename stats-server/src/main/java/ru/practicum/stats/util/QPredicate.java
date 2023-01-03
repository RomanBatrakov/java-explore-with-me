package ru.practicum.stats.util;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.practicum.stats.model.QHit.hit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QPredicate {
    private final List<Predicate> predicateList = new ArrayList<>();

    public <T> QPredicate add(T object, Function<T, Predicate> function) {
        if (object != null) {
            predicateList.add(function.apply(object));
        }
        return this;
    }

    public Predicate buildAnd() {
        return ExpressionUtils.allOf(predicateList);
    }

    public static QPredicate builder() {
        return new QPredicate();
    }

    public static Predicate createPredicate(List<String> uris, LocalDateTime start, LocalDateTime end) {
        return QPredicate.builder()
                .add(uris, hit.uri::in)
                .add(start, hit.timestamp::after)
                .add(end, hit.timestamp::before)
                .buildAnd();
    }
}