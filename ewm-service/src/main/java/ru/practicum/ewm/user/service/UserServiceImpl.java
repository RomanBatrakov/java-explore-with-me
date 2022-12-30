package ru.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exeption.ValidationException;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserById(Long id) {
        try {
            log.info("Getting user with id={}", id);
            return userMapper.toUserDto(userRepository.findById(id).get());
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("User with id %s is not found", id));
        }
    }

    @Override
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
        try {
            log.info("Creating user: {}", userDto);
            return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException(String.format("User name %s is already exist", userDto.getName()));
        }
    }

    @Override
    public void deleteUser(Long id) {
        try {
            log.info("Deleting user with id={}", id);
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException(String.format("User with id %s is not found", id));
        }
    }
}
