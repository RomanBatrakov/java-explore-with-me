package ru.practicum.ewm.reaction.dto;

import lombok.*;
import ru.practicum.ewm.reaction.model.ReactionType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReactionDto {
    private Long userId;
    private Long eventId;
    private ReactionType reaction;
}