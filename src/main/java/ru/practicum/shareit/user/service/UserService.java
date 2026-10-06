package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto create(NewUserDto newUserDto) {
        log.debug("Создание пользователя {}", newUserDto.getEmail());
        validateEmailUnique(newUserDto.getEmail(), null);

        User user = UserMapper.toUser(newUserDto);
        User savedUser = userRepository.save(user);
        log.info("Пользователь создан с id={}", savedUser.getId());
        return UserMapper.toUserDto(savedUser);
    }

    public UserDto update(Long userId, UpdateUserDto updateUserDto) {
        log.debug("Обновление пользователя с id={}", userId);
        User existing = getUserOrThrow(userId);

        if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().isBlank()) {
            validateEmailUnique(updateUserDto.getEmail(), userId);
        }

        User updated = UserMapper.updateUser(existing, updateUserDto);
        User saved = userRepository.save(updated);
        log.info("Пользователь с id={} обновлен", userId);
        return UserMapper.toUserDto(saved);
    }

    public UserDto getById(Long userId) {
        log.debug("Получение пользователя с id={}", userId);
        return UserMapper.toUserDto(getUserOrThrow(userId));
    }

    public List<UserDto> getAll() {
        log.debug("Получение всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public void delete(Long userId) {
        log.debug("Удаление пользователя с id={}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        userRepository.deleteById(userId);
        log.info("Пользователь удалён: id={}", userId);
    }


    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void validateEmailUnique(String email, Long excludeUserId) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }
        userRepository.findByEmail(email)
                .filter(u -> !Objects.equals(u.getId(), excludeUserId))
                .ifPresent(u -> {
                    log.warn("Email уже используется: {}", email);
                    throw new ConflictException("Email " + email + " уже используется");
                });
    }
}
