package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	@NotNull(message = "ID вещи обязателен")
	private long itemId;

	@NotNull(message = "Дата начала обязательна")
	@FutureOrPresent(message = "Дата начала не может быть в прошлом")
	private LocalDateTime start;

	@NotNull(message = "Дата окончания боязательна")
	@Future(message = "Дата окончания должна быть в будущем")
	private LocalDateTime end;
}
