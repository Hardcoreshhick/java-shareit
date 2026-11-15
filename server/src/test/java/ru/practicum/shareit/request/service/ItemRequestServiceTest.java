package ru.practicum.shareit.request.service;

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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        user1 = userService.create(NewUserDto.builder().name("User 1").email("u1@test.ru").build());
        user2 = userService.create(NewUserDto.builder().name("User 2").email("u2@test.ru").build());
    }

    @Test
    @DisplayName("Создание запроса: успешный сценарий")
    void createRequest_shouldReturnSavedRequest() {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Нужна дрель");

        ItemRequestDto saved = requestService.createRequest(user1.getId(), dto);

        assertNotNull(saved.getId());
        assertEquals("Нужна дрель", saved.getDescription());
        assertNotNull(saved.getCreated());
    }

    @Test
    @DisplayName("Создание запроса: пользователь не найден")
    void createRequest_shouldThrowExceptionIfUserNotFound() {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Нужна дрель");

        assertThrows(NotFoundException.class, () -> requestService.createRequest(999L, dto));
    }

    @Test
    @DisplayName("Получение своих запросов: сортировка от новых к старым")
    void getOwnRequests_shouldReturnRequestsOfUser() {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Запрос 1");
        requestService.createRequest(user1.getId(), dto);

        dto.setDescription("Запрос 2");
        requestService.createRequest(user1.getId(), dto);

        List<ItemRequestDto> requests = requestService.getOwnRequests(user1.getId());

        assertEquals(2, requests.size());
        assertEquals("Запрос 2", requests.get(0).getDescription());
        assertEquals("Запрос 1", requests.get(1).getDescription());
    }

    @Test
    @DisplayName("Получение чужих запросов: пагинация (from=5, size=10)")
    void getOtherRequests_shouldReturnPaginatedRequests() throws InterruptedException {
        for (int i = 1; i <= 15; i++) {
            NewItemRequestDto dto = new NewItemRequestDto();
            dto.setDescription("Запрос " + i);
            requestService.createRequest(user1.getId(), dto);
            Thread.sleep(10);
        }

        List<ItemRequestDto> requests = requestService.getOtherRequests(user2.getId(), 5, 10);

        assertEquals(10, requests.size());
        assertEquals("Запрос 10", requests.get(0).getDescription());
        assertEquals("Запрос 1", requests.get(9).getDescription());
    }

    @Test
    @DisplayName("Получение запроса по ID: успех и ошибка")
    void getRequestById_shouldReturnRequestOrThrow() {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Ищу стремянку");
        ItemRequestDto saved = requestService.createRequest(user1.getId(), dto);

        ItemRequestDto found = requestService.getRequestById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Ищу стремянку", found.getDescription());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(999L));
    }

    @Test
    @DisplayName("Получение запроса с вещами")
    void getRequestById_shouldReturnRequestWithItems() {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setDescription("Ищу дрель");
        ItemRequestDto savedRequest = requestService.createRequest(user1.getId(), requestDto);

        NewItemDto itemDto = new NewItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная");
        itemDto.setAvailable(true);
        itemDto.setRequestId(savedRequest.getId());
        ItemDto item = itemService.create(itemDto, user2.getId());

        ItemRequestDto found = requestService.getRequestById(savedRequest.getId());

        assertNotNull(found.getItems());
        assertEquals(1, found.getItems().size());
        assertEquals("Дрель", found.getItems().get(0).getName());
    }
}