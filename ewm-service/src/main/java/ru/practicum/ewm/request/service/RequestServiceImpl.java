package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exeption.ValidationException;
import ru.practicum.ewm.request.dao.RequestRepository;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm.request.model.RequestStatus.CONFIRMED;
import static ru.practicum.ewm.request.model.RequestStatus.PENDING;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public List<RequestDto> getAllUserRequests(Long id) {
        try {
            log.info("Getting user requests with userId={}", id);
            return requestRepository.findAllByRequesterId(id).stream()
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        log.info("Creating request with userId={}, eventId={}", userId, eventId);
        User user = userMapper.toUser(userService.getUserById(userId));
        Event event = eventMapper.toEvent(eventService.getEventById(eventId));
        if (Objects.equals(event.getInitiator().getId(), userId)
                && event.getPublishedOn() == null
                && event.getParticipantLimit() <= event.getConfirmedRequests())
            throw new ValidationException(String.format("Requester with id %s cant be initiator", userId));
        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(event.getRequestModeration() ? PENDING : CONFIRMED)
                .build();
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    //TODO: проработать правильность выбасываемого исключения
    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        try {
            log.info("Canceled request with userId={}, requestId={}", userId, requestId);
            Request request = requestRepository.findById(requestId).get();
            Long id = request.getRequester().getId();
            if (Objects.equals(userId, id)) requestRepository.deleteById(requestId);
            return requestMapper.toRequestDto(request);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Request with id %s is not found", requestId));
        }
    }
}