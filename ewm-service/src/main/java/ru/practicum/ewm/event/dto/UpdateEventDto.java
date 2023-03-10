package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDto {
    @NotNull(message = " is null")
    private Long eventId;
    @Size(min = 20, max = 2000, message = " has wrong size")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message = " has wrong size")
    private String description;
    @Future(message = " is not future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Boolean paid;
    private Long participantLimit;
    @Size(min = 3, max = 120, message = " has wrong size")
    private String title;
}
