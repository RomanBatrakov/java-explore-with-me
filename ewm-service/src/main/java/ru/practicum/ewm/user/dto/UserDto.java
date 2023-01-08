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
    @NotBlank(message = "Name is mandatory")
    @Size(max = 50, message = "Name is to long")
    private String name;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email is incorrect")
    private String email;
}
