package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.location.dto.LocationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotNull(message = "annotation is null")
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull(message = "category is null")
    private Long category;
    @NotNull(message = "description is null")
    @Size(min = 20, max = 7000)
    private String description;
    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull(message = "location is null")
    private LocationDto location;
    @NotNull(message = "paid is null")
    @Builder.Default
    private Boolean paid = false;
    @NotNull(message = "participantLimit is null")
    @Builder.Default
    private Long participantLimit = 0L;
    @NotNull(message = "requestModeration is null")
    @Builder.Default
    private Boolean requestModeration = true;
    @NotNull(message = "title is null")
    @Size(min = 3, max = 120)
    private String title;
}