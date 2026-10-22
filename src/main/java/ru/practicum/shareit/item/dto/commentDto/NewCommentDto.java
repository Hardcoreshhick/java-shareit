package ru.practicum.shareit.item.dto.commentDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewCommentDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
