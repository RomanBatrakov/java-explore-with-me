package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = " is blank or null")
    @Size(max = 50, message = " is to long")
    private String name;
    @NotBlank(message = " is blank or null")
    @Email(message = " is incorrect")
    private String email;
}
