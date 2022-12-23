package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/compilations")
@AllArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("POST request for path /admin/compilations with compilation: {}", newCompilationDto);
        return ResponseEntity.ok(compilationService.createCompilation(newCompilationDto));
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

    @DeleteMapping("/{compId}/pin")
    public void pinOffCompilation(@PathVariable("compId") Long id) {
        log.info("DELETE request for path /admin/compilations/{compId}/pin with compId={}", id);
        compilationService.pinOffCompilation(id);
    }

    @PatchMapping("/{compId}/pin")
    public void pinOnCompilation(@PathVariable("compId") Long id) {
        log.info("PATCH request for path /admin/compilations/{compId}/pin with compId={}", id);
        compilationService.pinOnCompilation(id);
    }

}
