package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @DisplayName("Сериализация ItemRequestDto в JSON")
    void testSerialize() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.of(2026, 11, 12, 10, 0, 0))
                .items(List.of())
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-11-12T10:00:00");
        assertThat(result).hasJsonPathArrayValue("$.items");
    }

    @Test
    @DisplayName("Десериализация JSON в ItemRequestDto")
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"description\":\"Нужна дрель\",\"created\":\"2026-11-12T10:00:00\",\"items\":[]}";

        ItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getCreated())
                .isEqualTo(LocalDateTime.of(2026, 11, 12, 10, 0, 0));
        assertThat(dto.getItems()).isEmpty();
    }
}