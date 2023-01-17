package ru.practicum.ewm.event.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface EventMapper {
    Event toEvent(EventDto eventDto);

    EventDto toEventDto(Event event);

    @Mapping(target = "category", ignore = true)
    Event fromNewEvent(NewEventDto newEventDto);

    @Mapping(target = "rating", source = "rating.rating")
    EventShortDto toEventShortDto(Event event);

    List<EventShortDto> toEventShortDtoList(List<Event> events);

    List<EventDto> toEventDtoList(List<Event> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    Event updateEventByAdmin(AdminUpdateEventDto adminUpdateEventDto, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "id", source = "eventId")
    Event updateEventByUser(UpdateEventDto updateEventDto, @MappingTarget Event event);
}