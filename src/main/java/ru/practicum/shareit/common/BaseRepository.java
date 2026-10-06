package ru.practicum.shareit.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public abstract class BaseRepository<T> {

    protected final Map<Long, T> storage = new HashMap<>();
    protected final AtomicLong nextId = new AtomicLong(1L);

    protected abstract Long getId(T entity);

    protected abstract void setId(T entity, Long id);

    public T save(T entity) {
        if (getId(entity) == null) {
            setId(entity, getNextId());
        }
        storage.put(getId(entity), entity);
        return entity;
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    protected Long getNextId() {
        return nextId.getAndIncrement();
    }
}
