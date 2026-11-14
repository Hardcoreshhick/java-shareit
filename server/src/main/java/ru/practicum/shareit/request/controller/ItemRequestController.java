package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                        @Valid @RequestBody NewItemRequestDto dto) {
        return requestService.createRequest(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherRequests(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return requestService.getOtherRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable @Positive Long requestId) {
        return requestService.getRequestById(requestId);
    }
}
