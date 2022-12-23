package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping(path = "/compilations")
@AllArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;
//TODO: поверить работу когда пина нет
    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                                   @RequestParam(required = false, defaultValue = "0")
                                                                   @PositiveOrZero int from,
                                                                   @RequestParam(required = false, defaultValue = "10")
                                                                   @Positive int size) {
        log.info("GET request for path /compilations");
        return ResponseEntity.ok(compilationService.getAllCompilations(pinned, PageRequest.of(from, size)));
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable("compId") Long id) {
        log.info("GET request for path /compilations/{compId} with id={}", id);
        return ResponseEntity.ok(compilationService.getCompilationById(id));
    }
}
