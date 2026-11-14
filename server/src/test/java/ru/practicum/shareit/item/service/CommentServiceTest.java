package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.commentDto.CommentDto;
import ru.practicum.shareit.item.dto.commentDto.NewCommentDto;
import ru.practicum.shareit.item.dto.itemDto.ItemDto;
import ru.practicum.shareit.item.dto.itemDto.NewItemDto;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingService bookingService;

    private UserDto owner;
    private UserDto booker;
    private ItemDto item;

    @BeforeEach
    void setUp() {
        owner = userService.create(NewUserDto.builder().name("Владелец").email("owner@test.ru").build());
        booker = userService.create(NewUserDto.builder().name("Арендатор").email("booker@test.ru").build());

        NewItemDto itemDto = new NewItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная");
        itemDto.setAvailable(true);
        item = itemService.create(itemDto, owner.getId());
    }

    @Test
    @DisplayName("Добавление комментария: успешный сценарий")
    void addComment_shouldSaveComment() {
        NewBookingDto bookingDto = new NewBookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto booking = bookingService.createBooking(bookingDto, booker.getId());
        bookingService.approveBooking(booking.getId(), owner.getId(), true);

        NewCommentDto commentDto = new NewCommentDto();
        commentDto.setText("Отличная дрель!");

        CommentDto savedComment = commentService.addComment(item.getId(), booker.getId(), commentDto);

        assertNotNull(savedComment.getId());
        assertEquals("Отличная дрель!", savedComment.getText());
        assertEquals("Арендатор", savedComment.getAuthorName());
    }

    @Test
    @DisplayName("Добавление комментария: пользователь не брал вещь (ошибка)")
    void addComment_shouldThrowExceptionWhenNotBooked() {
        NewCommentDto commentDto = new NewCommentDto();
        commentDto.setText("Отличная дрель!");

        assertThrows(ValidationException.class, () ->
                commentService.addComment(item.getId(), booker.getId(), commentDto)
        );
    }

    @Test
    @DisplayName("Добавление комментария: вещь не найдена (ошибка)")
    void addComment_shouldThrowExceptionWhenItemNotFound() {
        NewCommentDto commentDto = new NewCommentDto();
        commentDto.setText("Отличная дрель!");

        assertThrows(NotFoundException.class, () ->
                commentService.addComment(999L, booker.getId(), commentDto)
        );
    }

    @Test
    @DisplayName("Получение комментариев: пустой список ID")
    void getCommentsByItemIds_shouldReturnEmptyMap() {
        Map<Long, List<CommentDto>> result = commentService.getCommentsByItemIds(List.of());
        assertTrue(result.isEmpty());
    }
}