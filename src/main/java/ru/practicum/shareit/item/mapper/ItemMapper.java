package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
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
        item.setRequest(existing.getRequest());
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
}
