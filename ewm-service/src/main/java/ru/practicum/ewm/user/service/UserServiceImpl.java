package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exeption.ValidationException;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EventService eventService;

    @Override
    public UserDto getUserById(Long id) {
        try {
            log.info("Getting user with id={}", id);
            User user = userRepository.findById(id).get();
            setUserRating(user);
            return userMapper.toUserDto(user);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("User with id %s is not found", id));
        }
    }

    @Override
    public List<UserDto> getUsersByIds(Set<Long> ids, Pageable pageable) {
        try {
            log.info("Getting users list with such ids: {}", ids);
            return userRepository.findUsersByIdIn(ids, pageable).stream()
                    .peek(this::setUserRating)
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

    @Override
    public void setUserRating(User user) {
        OptionalDouble rating = eventService.getEventListByUser(user.getId(), Pageable.unpaged())
                .stream()
                .map(Event::getRating)
                .mapToDouble(Rating::getRating)
                .average();
        if (rating.isEmpty()) {
            user.setRating(0.0);
        } else {
            user.setRating(rating.getAsDouble());
        }
    }
}
