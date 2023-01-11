package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.location.dto.LocationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank(message = " is blank or null")
    @Size(min = 20, max = 2000, message = " has wrong size")
    private String annotation;
    @NotNull(message = " is null")
    private Long category;
    @NotBlank(message = " is blank or null")
    @Size(min = 20, max = 7000, message = " has wrong size")
    private String description;
    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull(message = " is null")
    private LocationDto location;
    @NotNull(message = " is null")
    @Builder.Default
    private Boolean paid = false;
    @NotNull(message = " is null")
    @Builder.Default
    private Long participantLimit = 0L;
    @NotNull(message = " is null")
    @Builder.Default
    private Boolean requestModeration = true;
    @NotNull(message = " is null")
    @Size(min = 3, max = 120, message = " has wrong size")
    private String title;
}