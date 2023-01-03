package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    private Long id;
    @NotBlank(message = "app is mandatory")
    @NotNull(message = "app is null")
    private String app;
    @NotBlank(message = "uri is mandatory")
    @NotNull(message = "uri is null")
    private String uri;
    @NotBlank(message = "ip is mandatory")
    @NotNull(message = "ip is null")
    private String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}