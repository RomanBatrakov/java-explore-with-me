package ru.practicum.ewm.user.service;

import java.util.List;

public interface UserService {
    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(long id, UserDto userDto);

    void deleteUser(long id);
}