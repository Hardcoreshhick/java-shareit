package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                @RequestBody @Valid NewItemRequestDto requestDto) {
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherRequests(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestClient.getOtherRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                             @PathVariable @Positive Long requestId) {
        return requestClient.getRequest(userId, requestId);
    }

}
