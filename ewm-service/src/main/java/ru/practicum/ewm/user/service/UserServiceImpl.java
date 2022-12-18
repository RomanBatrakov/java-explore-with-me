package ru.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserById(long id) {
        try {
            log.info("Getting user with id={}", id);
            return userMapper.toUserDto(userRepository.findById(id).get());
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("User with id %s is not found", id));
        }
    }

    //TODO: проверить этот метод на корректность работы
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(Set<Long> ids, Pageable pageable) {
        try {
            log.info("Getting users list with such ids: {}", ids);
            return userRepository.findUsersByIdIn(ids, pageable).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    public void deleteUser(long id) {
        try {
            log.info("Deleting user with id {}", id);
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException(String.format("User with id %s is not found", id));
        }
    }
}
