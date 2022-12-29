package ru.practicum.ewm.request.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "event", source = "event.id")
    RequestDto toRequestDto(Request request);
}