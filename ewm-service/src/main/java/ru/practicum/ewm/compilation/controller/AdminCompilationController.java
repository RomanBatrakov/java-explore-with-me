package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Object> deleteCompilation(@PathVariable("compId") Long id) {
        log.info("DELETE request for path /admin/compilations/{compId} with compId={}", id);
        compilationService.deleteCompilation(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public ResponseEntity<Object> deleteEventFromCompilation(@PathVariable("compId") Long id,
                                                             @PathVariable("eventId") Long eventId) {
        log.info("DELETE request for path /admin/compilations/{compId}/events/{eventId} " +
                "with compId={}, eventId={}", id, eventId);
        compilationService.deleteEventFromCompilation(id, eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public ResponseEntity<Object> addEventToCompilation(@PathVariable("compId") Long id,
                                                        @PathVariable("eventId") Long eventId) {
        log.info("PATCH request for path /admin/compilations/{compId}/events/{eventId} " +
                "with compId={}, eventId={}", id, eventId);
        compilationService.addEventToCompilation(id, eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{compId}/pin")
    public ResponseEntity<Object> pinOffCompilation(@PathVariable("compId") Long id) {
        log.info("DELETE request for path /admin/compilations/{compId}/pin with compId={}", id);
        compilationService.pinChangeCompilation(id, false);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{compId}/pin")
    public ResponseEntity<Object> pinOnCompilation(@PathVariable("compId") Long id) {
        log.info("PATCH request for path /admin/compilations/{compId}/pin with compId={}", id);
        compilationService.pinChangeCompilation(id, true);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}