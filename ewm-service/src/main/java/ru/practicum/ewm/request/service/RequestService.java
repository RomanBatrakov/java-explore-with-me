package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

public interface RequestService {
    List<RequestDto> getAllUserRequests(Long id);

    RequestDto createRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getUserRequests(Long userId, Long eventId);

    void rejectOtherRequests(Long eventId);

    RequestDto changeRequestStatus(Long userId, Long eventId, Long requestId, RequestStatus status);
}
