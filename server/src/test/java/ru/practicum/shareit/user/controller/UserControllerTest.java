package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Создание пользователя: успешный ответ")
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Иван");
        responseDto.setEmail("ivan@test.ru");

        when(userService.create(any(NewUserDto.class))).thenReturn(responseDto);

        NewUserDto requestDto = new NewUserDto();
        requestDto.setName("Иван");
        requestDto.setEmail("ivan@test.ru");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Получение пользователя по ID")
    void getUserById_shouldReturnUser() throws Exception {
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Иван");

        when(userService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateUser_shouldReturnOk() throws Exception {
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        when(userService.update(anyLong(), any(UpdateUserDto.class))).thenReturn(responseDto);

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setName("Новое имя");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers_shouldReturnList() throws Exception {
        when(userService.getAll()).thenReturn(List.of(new UserDto()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}