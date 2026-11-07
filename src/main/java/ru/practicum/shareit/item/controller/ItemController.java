package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.commentDto.CommentDto;
import ru.practicum.shareit.item.dto.commentDto.NewCommentDto;
import ru.practicum.shareit.item.dto.itemDto.ItemDto;
import ru.practicum.shareit.item.dto.itemDto.NewItemDto;
import ru.practicum.shareit.item.dto.itemDto.UpdateItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @Valid @RequestBody NewItemDto newItemDto) {
        return itemService.create(newItemDto, userId);
    }

    @PostMapping ("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody NewCommentDto dto) {
        return commentService.addComment(itemId, userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @PathVariable @Positive Long itemId,
                          @Valid @RequestBody UpdateItemDto updateItemDto) {
        return itemService.update(updateItemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemService.getAllByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
