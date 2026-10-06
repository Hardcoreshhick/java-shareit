package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.BaseRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Objects;

@Repository
public class ItemRepositoryImpl extends BaseRepository<Item> implements ItemRepository {

    @Override
    protected Long getId(Item entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Item entity, Long id) {
        entity.setId(id);
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        if (ownerId == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(item -> item.getOwner() != null
                        && Objects.equals(item.getOwner().getId(), ownerId))
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String lowerText = text.toLowerCase();
        return storage.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> (item.getName() != null
                        && item.getName().toLowerCase().contains(lowerText))
                        || (item.getDescription() != null
                        && item.getDescription().toLowerCase().contains(lowerText)))
                .toList();
    }
}
