package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.NewItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    @DisplayName("Создание вещи: пустое имя (ошибка 400)")
    void createItem_shouldReturnBadRequestWhenNameEmpty() throws Exception {
        NewItemDto requestDto = new NewItemDto();
        requestDto.setName(""); // Пустое имя
        requestDto.setDescription("Мощная");
        requestDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Создание вещи: успешный ответ")
    void createItem_shouldReturnOk() throws Exception {
        when(itemClient.create(any(Long.class), any(NewItemDto.class))).thenReturn(ResponseEntity.ok().build());

        NewItemDto requestDto = new NewItemDto();
        requestDto.setName("Дрель");
        requestDto.setDescription("Мощная");
        requestDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
}