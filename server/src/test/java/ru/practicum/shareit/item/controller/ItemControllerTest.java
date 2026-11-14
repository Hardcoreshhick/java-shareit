package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.commentDto.CommentDto;
import ru.practicum.shareit.item.dto.commentDto.NewCommentDto;
import ru.practicum.shareit.item.dto.itemDto.ItemDto;
import ru.practicum.shareit.item.dto.itemDto.NewItemDto;
import ru.practicum.shareit.item.dto.itemDto.UpdateItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("Создание вещи: успешный ответ")
    void createItem_shouldReturnItem() throws Exception {
        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Дрель");

        when(itemService.create(any(NewItemDto.class), anyLong())).thenReturn(responseDto);

        NewItemDto requestDto = new NewItemDto();
        requestDto.setName("Дрель");
        requestDto.setDescription("Мощная");
        requestDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Поиск вещей")
    void searchItems_shouldReturnList() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Дрель");

        when(itemService.search("Дрель")).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search?text=Дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    @DisplayName("Обновление вещи")
    void updateItem_shouldReturnOk() throws Exception {
        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        when(itemService.update(any(UpdateItemDto.class), anyLong(), anyLong())).thenReturn(responseDto);

        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setName("Новая дрель");

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение вещи по ID")
    void getItemById_shouldReturnItem() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(new ItemDto());

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение всех вещей владельца")
    void getAllByOwner_shouldReturnList() throws Exception {
        when(itemService.getAllByOwnerId(1L)).thenReturn(List.of(new ItemDto()));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Добавление комментария")
    void addComment_shouldReturnOk() throws Exception {
        CommentDto responseDto = CommentDto.builder()
                .id(1L)
                .text("Хорошая вещь")
                .authorName("Иван")
                .created(LocalDateTime.now())
                .build();

        when(commentService.addComment(anyLong(), anyLong(), any(NewCommentDto.class)))
                .thenReturn(responseDto);

        NewCommentDto commentDto = new NewCommentDto();
        commentDto.setText("Хорошая вещь");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Хорошая вещь"));
    }
}