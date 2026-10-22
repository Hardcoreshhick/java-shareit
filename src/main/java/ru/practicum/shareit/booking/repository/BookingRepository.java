package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE b.booker.id = :userId " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND (CAST(:startBefore AS timestamp) IS NULL OR b.start < :startBefore) " +
            "AND (CAST(:startAfter AS timestamp) IS NULL OR b.start > :startAfter) " +
            "AND (CAST(:endBefore AS timestamp) IS NULL OR b.end < :endBefore) " +
            "AND (CAST(:endAfter AS timestamp) IS NULL OR b.end > :endAfter) " +
            "ORDER BY b.start DESC")
    List<Booking> findBookingsByBooker(
            @Param("userId") Long userId,
            @Param("status") BookingStatus status,
            @Param("startBefore") LocalDateTime startBefore,
            @Param("startAfter") LocalDateTime startAfter,
            @Param("endBefore") LocalDateTime endBefore,
            @Param("endAfter") LocalDateTime endAfter
    );

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND (CAST(:startBefore AS timestamp) IS NULL OR b.start < :startBefore) " +
            "AND (CAST(:startAfter AS timestamp) IS NULL OR b.start > :startAfter) " +
            "AND (CAST(:endBefore AS timestamp) IS NULL OR b.end < :endBefore) " +
            "AND (CAST(:endAfter AS timestamp) IS NULL OR b.end > :endAfter) " +
            "ORDER BY b.start DESC")
    List<Booking> findBookingsByOwner(
            @Param("ownerId") Long ownerId,
            @Param("status") BookingStatus status,
            @Param("startBefore") LocalDateTime startBefore,
            @Param("startAfter") LocalDateTime startAfter,
            @Param("endBefore") LocalDateTime endBefore,
            @Param("endAfter") LocalDateTime endAfter
    );

    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker " +
            "WHERE b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND b.end < :now ORDER BY b.end DESC")
    List<Booking> findLastBookingForItem(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker " +
            "WHERE b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND b.start > :now ORDER BY b.start ASC")
    List<Booking> findNextBookingForItem(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);
}
