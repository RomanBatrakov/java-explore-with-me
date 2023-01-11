package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("POST request for path /admin/compilations with compilation: {}", newCompilationDto);
        return compilationService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable("compId") Long id) {
        log.info("DELETE request for path /admin/compilations/{compId} with compId={}", id);
        compilationService.deleteCompilation(id);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable("compId") Long id,
                                           @PathVariable("eventId") Long eventId) {
        log.info("DELETE request for path /admin/compilations/{compId}/events/{eventId} " +
                "with compId={}, eventId={}", id, eventId);
        compilationService.deleteEventFromCompilation(id, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable("compId") Long id,
                                      @PathVariable("eventId") Long eventId) {
        log.info("PATCH request for path /admin/compilations/{compId}/events/{eventId} " +
                "with compId={}, eventId={}", id, eventId);
        compilationService.addEventToCompilation(id, eventId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinOffCompilation(@PathVariable("compId") Long id) {
        log.info("PATCH request for path /admin/compilations/{compId}/pin with compId={}", id);
        compilationService.pinChangeCompilation(id, false);
    }

    @PatchMapping("/{compId}/pin")
    public void pinOnCompilation(@PathVariable("compId") Long id) {
        log.info("PATCH request for path /admin/compilations/{compId}/pin with compId={}", id);
        compilationService.pinChangeCompilation(id, true);
    }
}