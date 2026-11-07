package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDto createBooking(NewBookingDto dto, Long userId) {
        log.debug("Создание брони: вещь id={}, арендатор id={}", dto.getItemId(), userId);

        User booker = getUserOrThrow(userId);
        Item item = getItemOrThrow(dto.getItemId());

        validateBookingCreation(item, booker, dto);

        Booking booking = BookingMapper.toBooking(dto, item, booker);
        Booking saved = bookingRepository.save(booking);

        log.info("Бронь успешно создана: id={},", saved.getId());
        return BookingMapper.toDto(saved);
    }

    @Transactional
    public BookingDto approveBooking(Long bookingId, Long userId, boolean approved) {
        log.debug("Подтверждение брони id={}, владелец id={}, approved={}", bookingId, userId, approved);

        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Пользователь id={} пытается подтвердить чужую бронь id={}", userId, bookingId);
            throw new ForbiddenException("Только владелец вещи может подтвердить бронь");
        }
        if (booking.getStatus() != WAITING) {
            log.warn("Бронь id={} уже обработана. Текущий статус: {}", bookingId, booking.getStatus());
            throw new ConflictException("Бронь уже обработана");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : REJECTED);
        Booking saved = bookingRepository.save(booking);

        log.info("Бронь id={} изменена на статус {}", bookingId, saved.getStatus());
        return BookingMapper.toDto(saved);
    }

    public BookingDto getBookingById(Long bookingId, Long userId) {
        log.debug("Запрос брони id={} пользователем id={}", bookingId, userId);
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Пользователь id={} пытается получить доступ к чужой брони id={}", userId, bookingId);
            throw new ForbiddenException("Нет доступа к этой брони");
        }
        return BookingMapper.toDto(booking);
    }

    public List<BookingDto> getBookingsByBooker(Long userId, BookingState state) {
        log.debug("Запрос броней арендатора id={}, статус={}", userId, state);
        getUserOrThrow(userId);
        return BookingMapper.toDtoList(fetchBookings(userId, state, true));
    }

    public List<BookingDto> getBookingsByOwner(Long ownerId, BookingState state) {
        log.debug("Запрос броней владельца id={}, статус={}", ownerId, state);
        getUserOrThrow(ownerId);
        return BookingMapper.toDtoList(fetchBookings(ownerId, state, false));
    }

    private void validateBookingCreation(Item item, User booker, NewBookingDto dto) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new NotFoundException("Нельзя бронировать свою вещь");
        }
        if (dto.getStart().isAfter(dto.getEnd()) || dto.getStart().equals(dto.getEnd())) {
            throw new ValidationException("Дата начала брони должна быть раньше даты окончания");
        }
    }

    private List<Booking> fetchBookings(Long userId, BookingState state, boolean isBooker) {
        LocalDateTime now = LocalDateTime.now();
        BookingStatus status = null;
        LocalDateTime startBefore = null, startAfter = null, endBefore = null, endAfter = null;

        switch (state) {
            case WAITING -> status = BookingStatus.WAITING;
            case REJECTED -> status = BookingStatus.REJECTED;
            case PAST -> endBefore = now;
            case FUTURE -> startAfter = now;
            case CURRENT -> {
                startBefore = now;
                endAfter = now;
            }
            case ALL -> { /* null */ }
        }

        if (isBooker) {
            return bookingRepository.findBookingsByBooker(userId, status, startBefore, startAfter, endBefore, endAfter);
        } else {
            return bookingRepository.findBookingsByOwner(userId, status, startBefore, startAfter, endBefore, endAfter);
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с id " + bookingId + " не найдена"));
    }
}
