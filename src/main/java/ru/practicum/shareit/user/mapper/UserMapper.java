package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(NewUserDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static User updateUser(User existing, UpdateUserDto dto) {
        User user = new User();
        user.setId(existing.getId());
        user.setName(dto.getName() != null && !dto.getName().isBlank()
                ? dto.getName()
                : existing.getName());
        user.setEmail(dto.getEmail() != null && !dto.getEmail().isBlank()
                ? dto.getEmail()
                : existing.getEmail());
        return user;
    }
}
