package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.itemDto.ItemDto;
import ru.practicum.shareit.item.dto.itemDto.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

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
    @DisplayName("Создание бронирования: успешный сценарий")
    void createBooking_shouldSaveBooking() {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto saved = bookingService.createBooking(dto, booker.getId());

        assertNotNull(saved.getId());
        assertEquals(BookingStatus.WAITING, saved.getStatus());
        assertEquals(booker.getId(), saved.getBooker().getId());
    }

    @Test
    @DisplayName("Создание бронирования: вещь недоступна")
    void createBooking_shouldThrowExceptionWhenItemNotAvailable() {
        NewItemDto unavailableItemDto = new NewItemDto();
        unavailableItemDto.setName("Сломанная дрель");
        unavailableItemDto.setDescription("Не работает");
        unavailableItemDto.setAvailable(false);
        ItemDto unavailableItem = itemService.create(unavailableItemDto, owner.getId());

        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(unavailableItem.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    @DisplayName("Создание бронирования: нельзя бронировать свою вещь")
    void createBooking_shouldThrowExceptionWhenBookingOwnItem() {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(dto, owner.getId()));
    }

    @Test
    @DisplayName("Подтверждение бронирования: успех и ошибки")
    void approveBooking_shouldWorkCorrectly() {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto saved = bookingService.createBooking(dto, booker.getId());

        BookingDto approved = bookingService.approveBooking(saved.getId(), owner.getId(), true);
        assertEquals(BookingStatus.APPROVED, approved.getStatus());
        assertThrows(Exception.class, () -> bookingService.approveBooking(saved.getId(), owner.getId(), true));
    }

    @Test
    @DisplayName("Получение броней пользователя: ALL и WAITING")
    void getBookingsByBooker_shouldReturnList() {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(dto, booker.getId());

        List<BookingDto> all = bookingService.getBookingsByBooker(booker.getId(), BookingState.ALL);
        assertEquals(1, all.size());

        List<BookingDto> waiting = bookingService.getBookingsByBooker(booker.getId(), BookingState.WAITING);
        assertEquals(1, waiting.size());

        List<BookingDto> rejected = bookingService.getBookingsByBooker(booker.getId(), BookingState.REJECTED);
        assertEquals(0, rejected.size());
    }

    @Test
    @DisplayName("Получение броней пользователя: PAST, FUTURE, CURRENT")
    void getBookingsByBooker_shouldFilterByState() {
        NewBookingDto futureDto = new NewBookingDto();
        futureDto.setItemId(item.getId());
        futureDto.setStart(LocalDateTime.now().plusDays(5));
        futureDto.setEnd(LocalDateTime.now().plusDays(6));
        bookingService.createBooking(futureDto, booker.getId());

        List<BookingDto> future = bookingService.getBookingsByBooker(booker.getId(), BookingState.FUTURE);
        assertEquals(1, future.size());

        List<BookingDto> past = bookingService.getBookingsByBooker(booker.getId(), BookingState.PAST);
        assertEquals(0, past.size());

        List<BookingDto> current = bookingService.getBookingsByBooker(booker.getId(), BookingState.CURRENT);
        assertEquals(0, current.size());
    }

    @Test
    @DisplayName("Получение броней владельца")
    void getBookingsByOwner_shouldReturnList() {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(dto, booker.getId());

        List<BookingDto> bookings = bookingService.getBookingsByOwner(owner.getId(), BookingState.ALL);
        assertEquals(1, bookings.size());
    }

    @Test
    @DisplayName("Получение брони по ID: чужой пользователь (ошибка)")
    void getBookingById_shouldThrowExceptionForStranger() {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto saved = bookingService.createBooking(dto, booker.getId());

        UserDto stranger = userService.create(NewUserDto.builder().name("Чужой").email("stranger@test.ru").build());

        assertThrows(ForbiddenException.class, () ->
                bookingService.getBookingById(saved.getId(), stranger.getId())
        );
    }

    @Test
    @DisplayName("Подтверждение бронирования: отказ (REJECTED)")
    void approveBooking_shouldRejectBooking() {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto saved = bookingService.createBooking(dto, booker.getId());

        BookingDto rejected = bookingService.approveBooking(saved.getId(), owner.getId(), false);
        assertEquals(BookingStatus.REJECTED, rejected.getStatus());
    }

    @Test
    @DisplayName("Получение броней пользователя: REJECTED")
    void getBookingsByBooker_shouldReturnRejected() {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto saved = bookingService.createBooking(dto, booker.getId());

        bookingService.approveBooking(saved.getId(), owner.getId(), false);

        List<BookingDto> rejected = bookingService.getBookingsByBooker(booker.getId(), BookingState.REJECTED);
        assertEquals(1, rejected.size());
    }

    @Test
    @DisplayName("Получение брони по ID: не найдена (ошибка)")
    void getBookingById_shouldThrowExceptionWhenNotFound() {
        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(999L, booker.getId())
        );
    }
}