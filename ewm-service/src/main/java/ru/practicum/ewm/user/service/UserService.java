package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(long id);

    List<UserDto> getUsersByIds(List<Long> ids, Pageable pageable);

    UserDto createUser(UserDto userDto);

    void deleteUser(long id);
}