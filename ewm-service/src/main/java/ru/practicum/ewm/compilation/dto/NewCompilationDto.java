package ru.practicum.ewm.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private Long id;
    @NotNull(message = "Title is null")
    @NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title is to long")
    private String title;
    private boolean pinned;
    private List<Long> events;
}