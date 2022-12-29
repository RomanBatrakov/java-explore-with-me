package ru.practicum.ewm.event.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface EventMapper {
    Event toEvent(EventDto eventDto);

    EventDto toEventDto(Event event);

    Event toNewEvent(NewEventDto newEventDto);

    EventShortDto toEventShortDto(Event event);

    List<EventShortDto> toEventShortDtoList(List<Event> events);
    List<EventDto> toEvenDtoList(List<Event> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event updateEventByAdmin(AdminUpdateEventDto adminUpdateEventDto, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event updateEventByUser(UpdateEventDto updateEventDto, @MappingTarget Event event);

}