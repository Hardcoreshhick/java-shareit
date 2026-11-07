package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.commentDto.CommentDto;
import ru.practicum.shareit.item.dto.itemDto.ItemDto;
import ru.practicum.shareit.item.dto.itemDto.ItemView;
import ru.practicum.shareit.item.dto.itemDto.NewItemDto;
import ru.practicum.shareit.item.dto.itemDto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final BookingRepository bookingRepository;

    @Transactional
    public ItemDto create(NewItemDto newItemDto, Long userId) {
        log.debug("Создание вещи: name={}, userId={}", newItemDto.getName(), userId);
        User owner = getUserOrThrow(userId);

        Item item = ItemMapper.toItem(newItemDto);
        item.setOwner(owner);
        Item saved = itemRepository.save(item);
        log.info("Вещь создана: id={}, ownerId={}", saved.getId(), userId);
        return ItemMapper.toItemDto(saved);
    }

    @Transactional
    public ItemDto update(UpdateItemDto updateItemDto, Long itemId, Long userId) {
        log.debug("Обновление вещи: id={}, userId={}", itemId, userId);
        Item existing = getItemOrThrow(itemId);
        checkOwner(existing, userId);

        Item updated = ItemMapper.updateItem(existing, updateItemDto);
        Item saved = itemRepository.save(updated);
        log.info("Вещь обновлена: id={}", itemId);
        return ItemMapper.toItemDto(saved);
    }

    public ItemDto getById(Long itemId, Long userId) {
        log.debug("Получение вещи: id={}, userId={}", itemId, userId);
        Item item = getItemOrThrow(itemId);

        List<CommentDto> comments = commentService.getCommentsByItemId(itemId);

        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> lastBookings = bookingRepository.findLastBookingForItem(itemId, now);
            List<Booking> nextBookings = bookingRepository.findNextBookingForItem(itemId, now);

            if (!lastBookings.isEmpty()) {
                lastBooking = lastBookings.get(0).getEnd();
            }
            if (!nextBookings.isEmpty()) {
                nextBooking = nextBookings.get(0).getStart();
            }
        }

        // 3. Отдаем всё в маппер
        return ItemMapper.toDto(item, comments, lastBooking, nextBooking);
    }

    public List<ItemDto> getAllByOwnerId(Long userId) {
        log.debug("Получение вещей владельца (проекция): userId={}", userId);

        List<ItemView> views = itemRepository.findAllByOwnerIdProjected(userId);

        return views.stream()
                .map(view -> {
                    ItemDto dto = new ItemDto();
                    dto.setId(view.getId());
                    dto.setName(view.getName());
                    dto.setDescription(view.getDescription());
                    dto.setAvailable(view.getAvailable());
                    dto.setLastBooking(view.getLastBookingEnd());
                    dto.setNextBooking(view.getNextBookingStart());
                    dto.setComments(commentService.getCommentsByItemId(view.getId()));

                    return dto;
                })
                .toList();
    }

    public List<ItemDto> search(String text) {
        log.debug("Поиск вещей: text={}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void checkOwner(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Пользователь {} пытается изменить чужую вещь {}", userId, item.getId());
            throw new NotFoundException("Вещь с id " + item.getId() + " не найдена у пользователя " + userId);
        }
    }
}
