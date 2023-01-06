package ru.practicum.ewm.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@AllArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<RequestDto>> getAllUserRequests(@PathVariable("userId") Long id) {
        log.info("GET request for path /users/{userId}/requests with userId={}", id);
        return ResponseEntity.ok(requestService.getAllUserRequests(id));
    }

    @PostMapping
    public ResponseEntity<RequestDto> createRequest(@PathVariable("userId") Long userId,
                                                    @RequestParam Long eventId) {
        log.info("POST request for path /users/{userId}/requests with userId={}, eventId={}", userId, eventId);
        return ResponseEntity.ok(requestService.createRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<RequestDto> cancelRequest(@PathVariable("userId") Long userId,
                                                    @PathVariable("requestId") Long requestId) {
        log.info("PATCH request for path /users/{userId}/requests/{requestId}/cancel with" +
                " userId={}, requestId={}", userId, requestId);
        return ResponseEntity.ok(requestService.cancelRequest(userId, requestId));
    }
}