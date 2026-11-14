package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.NewUserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    @DisplayName("Создание по льзователя: успешный ответ")
    void createUser_shouldReturnOk() throws Exception {
        when(userClient.create(any(NewUserDto.class))).thenReturn(ResponseEntity.ok().build());

        NewUserDto requestDto = new NewUserDto();
        requestDto.setName("Иван");
        requestDto.setEmail("ivan@test.ru");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Создание пользователя: пустое имя (ошибка 400)")
    void createUser_shouldReturnBadRequestWhenNameEmpty() throws Exception {
        NewUserDto requestDto = new NewUserDto();
        requestDto.setName(""); // Пустое имя!
        requestDto.setEmail("ivan@test.ru");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Создание пользователя: невалидный email (ошибка 400)")
    void createUser_shouldReturnBadRequestWhenEmailInvalid() throws Exception {
        NewUserDto requestDto = new NewUserDto();
        requestDto.setName("Иван");
        requestDto.setEmail("not-an-email"); // Невалидный email

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}