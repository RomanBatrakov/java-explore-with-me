package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto getUserById(Long id);

    List<UserDto> getUsersByIds(Set<Long> ids, Pageable pageable);

    UserDto createUser(UserDto userDto);

    void deleteUser(Long id);

    void setUserRating(User user);
}
