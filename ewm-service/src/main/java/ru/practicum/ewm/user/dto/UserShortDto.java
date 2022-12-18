package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {
    //TODO: возможно и не нужен он
    private long id;
    @NotBlank(message = "Name is mandatory")
    @Size(max = 50, message = "Name is to long")
    private String name;
}
