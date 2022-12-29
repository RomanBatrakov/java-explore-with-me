package ru.practicum.ewm.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dao.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final EventService eventService;

    //TODO: проверить - добавить добавление событий в подборки
    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Pageable pageable) {
        log.info("Getting all compilations with pinned={}", pinned);
        if (pinned == null) {
            return compilationMapper.toCompilationDtoList(compilationRepository.findAll());
        } else {
            return compilationMapper.toCompilationDtoList(compilationRepository.findAllByPinnedIs(pinned));
        }
    }

    //TODO: проверить - добавить добавление событий в подборку
    @Override
    public CompilationDto getCompilationById(Long id) {
        log.info("Getting compilation with id={}", id);
        return compilationMapper.toCompilationDto(compilationRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException(String.format("compilation with id %s is not found", id))));
    }

    //TODO: проверить - добавить добавление событий из подборки в бд
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

    @Override
    public void deleteEventFromCompilation(Long id, Long eventId) {
        log.info("Deleting event with eventId={} from compilation with id={}", eventId, id);
        Compilation compilation = compilationMapper.toCompilation(getCompilationById(id));
        Event event = eventMapper.toEvent(eventService.getEventById(eventId));
        compilation.getEvents().remove(event);
    }

    @Override
    public void addEventToCompilation(Long id, Long eventId) {
        log.info("Adding event with eventId={} from compilation with id={}", eventId, id);
        Compilation compilation = compilationMapper.toCompilation(getCompilationById(id));
        Event event = eventMapper.toEvent(eventService.getEventById(eventId));
        compilation.getEvents().add(event);
    }

    @Override
    public void pinChangeCompilation(Long id, Boolean pinned) {
        log.info("Pin {} compilation with id={}", pinned, id);
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException(String.format("Compilation with id %s is not found", id)));
        compilation.setPinned(pinned);
        compilationRepository.save(compilation);
    }
}