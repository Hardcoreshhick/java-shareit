package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto create(NewItemDto newItemDto, Long userId) {
        log.debug("Создание вещи: name={}, userId={}", newItemDto.getName(), userId);
        User owner = getUserOrThrow(userId);

        Item item = ItemMapper.toItem(newItemDto);
        item.setOwner(owner);
        Item saved = itemRepository.save(item);
        log.info("Вещь создана: id={}, ownerId={}", saved.getId(), userId);
        return ItemMapper.toItemDto(saved);
    }

    public ItemDto update(UpdateItemDto updateItemDto, Long itemId, Long userId) {
        log.debug("Обновление вещи: id={}, userId={}", itemId, userId);
        Item existing = getItemOrThrow(itemId);
        checkOwner(existing, userId);

        Item updated = ItemMapper.updateItem(existing, updateItemDto);
        Item saved = itemRepository.save(updated);
        log.info("Вещь обновлена: id={}", itemId);
        return ItemMapper.toItemDto(saved);
    }

    public ItemDto getById(Long itemId) {
        log.debug("Получение вещи: id={}", itemId);
        return ItemMapper.toItemDto(getItemOrThrow(itemId));
    }

    public List<ItemDto> getAllByOwnerId(Long userId) {
        log.debug("Получение вещей владельца: userId={}", userId);
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public List<ItemDto> search(String text) {
        log.debug("Поиск вещей: text={}", text);
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
