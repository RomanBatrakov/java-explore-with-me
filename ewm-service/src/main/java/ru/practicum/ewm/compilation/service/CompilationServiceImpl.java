package ru.practicum.ewm.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dao.CompilationRepository;
import ru.practicum.ewm.compilation.dao.EventsCompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.model.Event;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventsCompilationRepository eventsCompilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;

    //    TODO: добавить добавление событий в подборки
    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, PageRequest pageRequest) {
        log.info("Getting all compilations with pinned={}", pinned);
        return compilationRepository.findAllByPinnedIs(pinned).stream()
//                .forEach(c -> c.setEvents(eventsCompilationRepository::findEventsByCompilation)
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    //TODO: добавить добавление событий в подборку
    @Override
    public CompilationDto getCompilationById(Long id) {
        try {
            log.info("Getting compilation with id={}", id);
            return compilationMapper.toCompilationDto(compilationRepository.findById(id).get());
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Compilation with id %s is not found", id));
        }
    }

    //TODO: добавить добавление событий из подборки в бд
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.info("Creating compilation: {}", newCompilationDto);
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        List<Event> events = eventService.eventsIdsToEvents(newCompilationDto.getEvents());
        compilation.setEvents(events);
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long id) {
        try {
            log.info("Deleting compilation with id={}", id);
            compilationRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException(String.format("Compilation with id %s is not found", id));
        }
    }

    //TODO: реализовать метод
    @Override
    public void deleteEventFromCompilation(Long id, Long eventId) {

    }

    //TODO: реализовать метод
    @Override
    public void addEventToCompilation(Long id, Long eventId) {

    }

    @Override
    public void pinOffCompilation(Long id) {
        try {
            log.info("Pin off compilation with id={}", id);
            Compilation compilation = compilationRepository.findById(id).get();
            compilation.setPinned(false);
            compilationRepository.save(compilation);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Compilation with id %s is not found", id));
        }
    }

    @Override
    public void pinOnCompilation(Long id) {
        try {
            log.info("Pin on compilation with id={}", id);
            Compilation compilation = compilationRepository.findById(id).get();
            compilation.setPinned(true);
            compilationRepository.save(compilation);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Compilation with id %s is not found", id));
        }
    }
}
