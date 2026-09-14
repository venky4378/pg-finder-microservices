package com.pgfinder.user.mapper;

import com.pgfinder.user.dto.UserRequestDto;
import com.pgfinder.user.dto.UserResponseDto;
import com.pgfinder.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface UserMapper {
    User toEntity(UserRequestDto userRequestDto);
    UserResponseDto toResponse(User user);

}
