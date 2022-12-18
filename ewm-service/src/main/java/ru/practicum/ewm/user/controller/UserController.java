package ru.practicum.ewm.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
//TODO: роверить метод, что корректно список выдает в дефолте
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestParam(required = false) Set<Long> ids,
                                                       @RequestParam(required = false, defaultValue = "0")
                                                       @PositiveOrZero int from,
                                                       @RequestParam(required = false, defaultValue = "10")
                                                       @Positive int size) {
        log.info("GET request for path /admin/users with userIds={}, from={}, size={}", ids, from, size);
        return ResponseEntity.ok(userService.getUsersByIds(ids, PageRequest.of(from, size)));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("POST request for path /admin/users with user: {}", userDto);
        return ResponseEntity.ok(userService.createUser(userDto));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long id) {
        log.info("DELETE request for path /admin/users/{userId} with userId={}", id);
        userService.deleteUser(id);
    }
}