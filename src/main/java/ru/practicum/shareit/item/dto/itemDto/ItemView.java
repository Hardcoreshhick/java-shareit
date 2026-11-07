package ru.practicum.shareit.item.dto.itemDto;

import java.time.LocalDateTime;

public interface ItemView {
    Long getId();

    String getName();

    String getDescription();

    Boolean getAvailable();

    LocalDateTime getLastBookingEnd();

    LocalDateTime getNextBookingStart();
}
