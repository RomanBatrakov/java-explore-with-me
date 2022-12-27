package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> getAllUserRequests(Long id);

    RequestDto createRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getUserRequests(Long userId, Long eventId);

    RequestDto confirmRequest(Long userId, Long eventId, Long requestId);

    RequestDto rejectRequest(Long userId, Long eventId, Long requestId);

    void rejectOtherRequests(Long eventId);
}
