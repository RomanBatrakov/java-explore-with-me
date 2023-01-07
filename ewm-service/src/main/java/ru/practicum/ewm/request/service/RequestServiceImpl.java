package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.EventDto;
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
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.request.model.RequestStatus.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
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
        newRequestValidator(userId, event);
        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now().withNano(0))
                .status(event.getRequestModeration() || event.getParticipantLimit() != 0 ? PENDING : CONFIRMED)
                .build();
        if (request.getStatus().equals(CONFIRMED)) eventService.checkParticipantLimit(event);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        try {
            log.info("Canceled request with userId={}, requestId={}", userId, requestId);
            Request request = requestRepository.findById(requestId).get();
            Long id = request.getRequester().getId();
            if (Objects.equals(userId, id)) {
                request.setStatus(CANCELED);
                return requestMapper.toRequestDto(requestRepository.save(request));
            } else {
                throw new ValidationException("Wrong user or request id");
            }
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Request with id %s is not found", requestId));
        }
    }

    @Override
    public List<RequestDto> getUserRequests(Long userId, Long eventId) {
        log.info("Getting event requests with userId={}, eventId={}", userId, eventId);
        EventDto eventDto = eventService.getEventById(eventId);
        if (!eventDto.getInitiator().getId().equals(userId)) throw new ValidationException("Wrong initiator");
        try {
            return requestRepository.findAllByEvent_Id(eventId).stream()
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public RequestDto changeRequestStatus(Long userId, Long eventId, Long requestId, RequestStatus status) {
        log.info("{} event request with eventId={}, userId={}, requestId={}", status, eventId, userId, requestId);
        Event event = eventMapper.toEvent(eventService.getEventById(eventId));
        try {
            Request request = requestRepository.findById(requestId).get();
            if (userId.equals(event.getInitiator().getId()) && request.getEvent().getId().equals(event.getId())) {
                request.setStatus(status);
                if (status.equals(CONFIRMED)) eventService.checkParticipantLimit(event);
                return requestMapper.toRequestDto(requestRepository.save(request));
            } else {
                throw new ValidationException(String.format("User with id %s is not initiator or wrong event/request",
                        userId));
            }
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Request with id %s is not found", requestId));
        }
    }


    @Override
    public void rejectOtherRequests(Long eventId) {
        log.info("Rejecting event request with eventId={}", eventId);
        try {
            List<Request> requests = requestRepository.findAllByEvent_IdAndStatus(eventId, PENDING).stream()
                    .peek(x -> x.setStatus(REJECTED))
                    .collect(Collectors.toList());
            requestRepository.saveAll(requests);
        } catch (NoSuchElementException e) {
            log.info("No more PENDING requests for eventId={}", eventId);
        }
    }

    private void newRequestValidator(Long userId, Event event) {
        log.info("Validating event request with userId={}, event: {}", userId, event);
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException(String.format("Requester with id %s cant be initiator", userId));
        } else if (event.getPublishedOn() == null) {
            throw new ValidationException("Event is not published");
        } else if ((event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests())) {
            throw new ValidationException("Event is full");
        }
    }

    @Override
    public void setConfirmedRequestsFromDb(List<Event> events) {
        Map<Event, Long> confirmedRequestsCountByEvent = requestRepository.findAllByEventInAndStatus(events, CONFIRMED)
                .stream()
                .collect(Collectors.groupingBy(Request::getEvent, Collectors.counting()));

        events.forEach(event -> event.setConfirmedRequests(
                confirmedRequestsCountByEvent.getOrDefault(event, 0L)));
    }
}