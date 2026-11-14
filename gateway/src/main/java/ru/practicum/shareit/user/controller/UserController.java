package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody NewUserDto newUserDto) {
        return userClient.create(newUserDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @Positive Long userId,
                                         @Valid @RequestBody UpdateUserDto updateUserDto) {
        return userClient.update(userId, updateUserDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable @Positive Long userId) {
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable @Positive Long userId) {
        return userClient.delete(userId);
    }
}
