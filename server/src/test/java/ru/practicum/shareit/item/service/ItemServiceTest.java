package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.itemDto.ItemDto;
import ru.practicum.shareit.item.dto.itemDto.NewItemDto;
import ru.practicum.shareit.item.dto.itemDto.UpdateItemDto;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    private UserDto owner;
    private UserDto otherUser;

    @BeforeEach
    void setUp() {
        owner = userService.create(NewUserDto.builder().name("Владелец").email("owner@test.ru").build());
        otherUser = userService.create(NewUserDto.builder().name("Другой").email("other@test.ru").build());
    }

    @Test
    @DisplayName("Создание вещи: успешный сценарий")
    void createItem_shouldSaveItem() {
        NewItemDto dto = new NewItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);

        ItemDto saved = itemService.create(dto, owner.getId());

        assertNotNull(saved.getId());
        assertEquals("Дрель", saved.getName());
        assertEquals("Мощная", saved.getDescription());
        assertTrue(saved.getAvailable());
    }

    @Test
    @DisplayName("Обновление вещи: успешный сценарий")
    void updateItem_shouldUpdateFields() {
        NewItemDto createDto = new NewItemDto();
        createDto.setName("Дрель");
        createDto.setDescription("Мощная");
        createDto.setAvailable(true);
        ItemDto saved = itemService.create(createDto, owner.getId());

        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setName("Новая дрель");

        ItemDto updated = itemService.update(updateDto, saved.getId(), owner.getId());

        assertEquals("Новая дрель", updated.getName());
        assertEquals("Мощная", updated.getDescription()); // Не менялось
    }

    @Test
    @DisplayName("Обновление вещи: чужой пользователь")
    void updateItem_shouldThrowExceptionWhenNotOwner() {
        NewItemDto createDto = new NewItemDto();
        createDto.setName("Дрель");
        createDto.setDescription("Мощная");
        createDto.setAvailable(true);
        ItemDto saved = itemService.create(createDto, owner.getId());

        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setName("Взлом");

        assertThrows(NotFoundException.class, () ->
                itemService.update(updateDto, saved.getId(), otherUser.getId())
        );
    }

    @Test
    @DisplayName("Получение вещи по ID")
    void getItemById_shouldReturnItem() {
        NewItemDto createDto = new NewItemDto();
        createDto.setName("Дрель");
        createDto.setDescription("Мощная");
        createDto.setAvailable(true);
        ItemDto saved = itemService.create(createDto, owner.getId());

        ItemDto found = itemService.getById(saved.getId(), owner.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    @DisplayName("Поиск вещей")
    void searchItems_shouldReturnOnlyAvailableAndMatching() {
        NewItemDto item1 = new NewItemDto();
        item1.setName("Молоток");
        item1.setDescription("Тяжелый");
        item1.setAvailable(true);
        itemService.create(item1, owner.getId());

        NewItemDto item2 = new NewItemDto();
        item2.setName("Дрель");
        item2.setDescription("Мощная");
        item2.setAvailable(false);
        itemService.create(item2, owner.getId());

        List<ItemDto> found = itemService.search("Молоток");

        assertEquals(1, found.size());
        assertEquals("Молоток", found.getFirst().getName());
    }

    @Test
    @DisplayName("Получение всех вещей владельца")
    void getAllByOwnerId_shouldReturnList() {
        NewItemDto dto = new NewItemDto();
        dto.setName("Вещь 1");
        dto.setDescription("Описание 1");
        dto.setAvailable(true);
        itemService.create(dto, owner.getId());

        List<ItemDto> items = itemService.getAllByOwnerId(owner.getId());
        assertEquals(1, items.size());
    }

    @Test
    @DisplayName("Поиск вещей: пустой запрос")
    void searchItems_shouldReturnEmptyListForBlankText() {
        List<ItemDto> found = itemService.search("   ");
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Получение вещи по ID: не найдена (ошибка)")
    void getItemById_shouldThrowExceptionWhenNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.getById(999L, owner.getId()));
    }

    @Test
    @DisplayName("Обновление вещи: частичное обновление (только имя)")
    void updateItem_shouldUpdateOnlyName() {
        NewItemDto createDto = new NewItemDto();
        createDto.setName("Дрель");
        createDto.setDescription("Мощная");
        createDto.setAvailable(true);
        ItemDto saved = itemService.create(createDto, owner.getId());

        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setName("Новая дрель");

        ItemDto updated = itemService.update(updateDto, saved.getId(), owner.getId());

        assertEquals("Новая дрель", updated.getName());
        assertEquals("Мощная", updated.getDescription());
        assertTrue(updated.getAvailable());
    }

    @Test
    @DisplayName("Создание вещи: запрос не найден (ошибка)")
    void createItem_shouldThrowExceptionWhenRequestNotFound() {
        NewItemDto dto = new NewItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);
        dto.setRequestId(999L);

        assertThrows(NotFoundException.class, () -> itemService.create(dto, owner.getId()));
    }

    @Test
    @DisplayName("Создание вещи: пользователь не найден (ошибка)")
    void createItem_shouldThrowExceptionWhenUserNotFound() {
        NewItemDto dto = new NewItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.create(dto, 999L));
    }
}