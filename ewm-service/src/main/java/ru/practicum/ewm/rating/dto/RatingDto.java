package ru.practicum.ewm.rating.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
    private Long likes;
    private Long dislikes;
    private Double rating;
}