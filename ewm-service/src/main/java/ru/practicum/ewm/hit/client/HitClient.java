package ru.practicum.ewm.hit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import ru.practicum.ewm.hit.model.Hit;
import ru.practicum.ewm.hit.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class HitClient {
    private final WebClient client;

    public HitClient(@Value("${stats-server.url}") String hitPath, WebClient.Builder builder) {
        client = builder.baseUrl(hitPath).build();
    }

    public void postHit(Hit hit) throws WebClientRequestException {
        log.info("Sending hit to stats service:{}", hit);
        client.post()
                .uri("/hit")
                .bodyValue(hit)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    log.info("Hit {} not saved with error {}", hit, error.statusCode());
                    return Mono.empty();
                })
                .bodyToMono(Void.class)
                .block();
    }

    public List<Stats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Finding stats from stats service with uris:{}", uris);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return client.get()
                .uri("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        start.format(formatter), end.format(formatter), uris, unique)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    log.info("Error: {}", error.statusCode());
                    return Mono.empty();
                })
                .toEntityList(Stats.class)
                .block()
                .getBody();
    }
}