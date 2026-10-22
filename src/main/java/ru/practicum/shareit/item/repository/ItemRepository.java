package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.itemDto.ItemView;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.available = true AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> search(@Param("text") String text);

    @Query("SELECT i.id as id, i.name as name, i.description as description, i.available as available, " +
            "(SELECT MAX(b.end) FROM Booking b WHERE b.item.id = i.id AND b.status = 'APPROVED' AND b.end < " +
            "CURRENT_TIMESTAMP) as lastBookingEnd, " +
            "(SELECT MIN(b.start) FROM Booking b WHERE b.item.id = i.id AND b.status = 'APPROVED' AND b.start > " +
            "CURRENT_TIMESTAMP) as nextBookingStart " +
            "FROM Item i WHERE i.owner.id = :ownerId")
    List<ItemView> findAllByOwnerIdProjected(@Param("ownerId") Long ownerId);
}
