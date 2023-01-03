package ru.practicum.ewm.hit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import ru.practicum.ewm.hit.model.Hit;

@Slf4j
@RestController
public class HitClient {
    private final WebClient client;

    public HitClient() {
        this.client = WebClient.create();
    }

    public void postHit(Hit hit) throws WebClientRequestException {
        log.info("Sending hit to stats service:{}", hit);
        String hitPostUrl = "http://localhost:9090/hit";
        client.post()
                .uri(hitPostUrl)
                .bodyValue(hit)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> {
                    log.info("Hit {} not saved with error {}", hit, error.statusCode());
                    return Mono.empty();
                })
                .bodyToMono(Hit.class)
                .block();
    }
}