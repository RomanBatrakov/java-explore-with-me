package ru.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public UserDto getUserById(long id) {
        return null;
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids, Pageable pageable) {
        return null;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        return null;
    }

    @Override
    public void deleteUser(long id) {

    }
}
