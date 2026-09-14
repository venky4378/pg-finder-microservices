package com.pgfinder.user.service;

import com.pgfinder.user.dto.UserRequestDto;
import com.pgfinder.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto userRequest);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(Long id, UserRequestDto userRequest);

    void deleteUser(Long id);
}