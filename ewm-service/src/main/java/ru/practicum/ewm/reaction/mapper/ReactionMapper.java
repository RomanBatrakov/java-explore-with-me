package ru.practicum.ewm.reaction.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.reaction.dto.ReactionDto;
import ru.practicum.ewm.reaction.model.Reaction;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ReactionMapper {
    @Mapping(target = "userId", source = "id.user.id")
    @Mapping(target = "eventId", source = "id.event.id")
    ReactionDto toReactionDto(Reaction reaction);
}
