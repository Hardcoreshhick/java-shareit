package ru.practicum.shareit.item.dto.itemDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewItemDto {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
