package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
public class ItemRequest {
    private Long id;

    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;

    @NotNull(message = "Не указан автор запроса")
    private User requestor;

    @NotNull(message = "Не указана дата создания")
    private LocalDateTime created;
}
