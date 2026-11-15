package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Создание пользователя: успешный сценарий")
    void createUser_shouldSaveUser() {
        NewUserDto dto = new NewUserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@test.ru");

        UserDto saved = userService.create(dto);

        assertNotNull(saved.getId());
        assertEquals("Иван", saved.getName());
        assertEquals("ivan@test.ru", saved.getEmail());
    }

    @Test
    @DisplayName("Создание пользователя: дубликат email")
    void createUser_shouldThrowExceptionWhenEmailExists() {
        NewUserDto dto1 = new NewUserDto();
        dto1.setName("Иван");
        dto1.setEmail("ivan@test.ru");
        userService.create(dto1);

        NewUserDto dto2 = new NewUserDto();
        dto2.setName("Петр");
        dto2.setEmail("ivan@test.ru");

        assertThrows(ConflictException.class, () -> userService.create(dto2));
    }

    @Test
    @DisplayName("Получение пользователя по ID: успех и ошибка")
    void getUserById_shouldReturnUserOrThrow() {
        NewUserDto dto = new NewUserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@test.ru");
        UserDto saved = userService.create(dto);

        UserDto found = userService.getById(saved.getId());
        assertEquals(saved.getId(), found.getId());

        assertThrows(NotFoundException.class, () -> userService.getById(999L));
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateUser_shouldUpdateFields() {
        NewUserDto dto = new NewUserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@test.ru");
        UserDto saved = userService.create(dto);

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setName("Иван Петров");

        UserDto updated = userService.update(saved.getId(), updateDto);

        assertEquals("Иван Петров", updated.getName());
        assertEquals("ivan@test.ru", updated.getEmail());
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser_shouldRemoveUser() {
        NewUserDto dto = new NewUserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@test.ru");
        UserDto saved = userService.create(dto);

        userService.delete(saved.getId());

        assertThrows(NotFoundException.class, () -> userService.getById(saved.getId()));
    }

    @Test
    @DisplayName("Обновление пользователя: не найден (ошибка)")
    void updateUser_shouldThrowExceptionWhenNotFound() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("Новое имя");
        assertThrows(NotFoundException.class, () -> userService.update(999L, dto));
    }

    @Test
    @DisplayName("Обновление пользователя: дубликат email")
    void updateUser_shouldThrowExceptionWhenEmailExists() {
        NewUserDto dto1 = new NewUserDto();
        dto1.setName("Иван");
        dto1.setEmail("ivan@test.ru");
        userService.create(dto1);

        NewUserDto dto2 = new NewUserDto();
        dto2.setName("Петр");
        dto2.setEmail("petr@test.ru");
        UserDto saved2 = userService.create(dto2);

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setEmail("ivan@test.ru");

        assertThrows(ConflictException.class, () -> userService.update(saved2.getId(), updateDto));
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAll_shouldReturnList() {
        NewUserDto dto = new NewUserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@test.ru");
        userService.create(dto);

        List<UserDto> users = userService.getAll();
        assertEquals(1, users.size());
    }
}