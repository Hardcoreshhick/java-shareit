package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "booker.id", target = "bookerId")
    @Mapping(target = "item.lastBooking", ignore = true)
    @Mapping(target = "item.nextBooking", ignore = true)
    @Mapping(target = "item.comments", ignore = true)
    BookingDto toDto(Booking booking);

    List<BookingDto> toDtoList(List<Booking> bookings);

    default Booking toBooking(NewBookingDto dto, Item item, User booker) {
        if (dto == null) return null;
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }
}