package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, NewItemDto newItemDto) {
        return post("", userId, newItemDto);
    }

    public ResponseEntity<Object> update(long userId, Long itemId, UpdateItemDto fields) {
        return patch("/" + itemId, userId, fields);
    }

    public ResponseEntity<Object> getById(long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllByOwner(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> search(String text) {
        return get("/search?text={text}", null, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(long userId, Long itemId, NewCommentDto comment) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}