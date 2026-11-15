package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.itemDto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    @Test
    void testMappersReturnNullOnNullInput() {
        assertNull(ItemMapper.toItemDto(null));
        assertNull(ItemMapper.toItem(null));
        assertNull(UserMapper.toUserDto(null));
        assertNull(UserMapper.toUser(null));
    }

    @Test
    void testUpdateItemWithNullDto() {
        Item existing = new Item();
        existing.setName("Старое имя");
        existing.setDescription("Старое описание");
        existing.setAvailable(true);

        Item result = ItemMapper.updateItem(existing, null);

        assertNotNull(result);
        assertEquals("Старое имя", result.getName());
    }

    @Test
    void testUpdateItemWithEmptyDto() {
        Item existing = new Item();
        existing.setName("Старое имя");
        existing.setDescription("Старое описание");
        existing.setAvailable(true);

        UpdateItemDto dto = new UpdateItemDto();

        Item result = ItemMapper.updateItem(existing, dto);

        assertEquals("Старое имя", result.getName());
        assertEquals("Старое описание", result.getDescription());
        assertTrue(result.getAvailable());
    }
}