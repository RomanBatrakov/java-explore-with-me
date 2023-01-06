package ru.practicum.ewm.location.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private Long id;
    @NotNull(message = "lat is null")
    @Min(value = -90, message = "lat cant be less than -90")
    @Max(value = 90, message = "lat cant be more than 90")
    private Float lat;
    @NotNull(message = "lon is null")
    @Min(value = -180, message = "lat cant be less than -180")
    @Max(value = 180, message = "lat cant be more than 180")
    private Float lon;
}