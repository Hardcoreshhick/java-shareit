package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCommentDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(max = 2000, message = "Комментарий не может быть длиннее 2000 символов")
    private String text;
}
