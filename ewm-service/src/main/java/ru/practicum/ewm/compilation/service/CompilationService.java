package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAllCompilations(Boolean pinned, Pageable pageable);

    CompilationDto getCompilationById(Long id);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long id);

    void deleteEventFromCompilation(Long id, Long eventId);

    void addEventToCompilation(Long id, Long eventId);

    void pinChangeCompilation(Long id, Boolean pinned);
}
