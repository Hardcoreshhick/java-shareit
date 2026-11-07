package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.commentDto.CommentDto;
import ru.practicum.shareit.item.dto.itemDto.ItemDto;
import ru.practicum.shareit.item.dto.itemDto.NewItemDto;
import ru.practicum.shareit.item.dto.itemDto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(NewItemDto dto) {
        if (dto == null) {
            return null;
        }
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static Item updateItem(Item existing, UpdateItemDto dto) {
        if (dto == null) {
            return existing;
        }
        Item item = new Item();
        item.setId(existing.getId());
        item.setOwner(existing.getOwner());
        item.setName(dto.getName() != null && !dto.getName().isBlank()
                ? dto.getName()
                : existing.getName());
        item.setDescription(dto.getDescription() != null && !dto.getDescription().isBlank()
                ? dto.getDescription()
                : existing.getDescription());
        item.setAvailable(dto.getAvailable() != null
                ? dto.getAvailable()
                : existing.getAvailable());
        return item;
    }

    public static ItemDto toDto(Item item,
                                List<CommentDto> comments,
                                LocalDateTime lastBooking,
                                LocalDateTime nextBooking) {
        if (item == null) return null;

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }
}

