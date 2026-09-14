package com.pgfinder.user.serviceImpl;

import com.pgfinder.user.dto.UserRequestDto;
import com.pgfinder.user.dto.UserResponseDto;
import com.pgfinder.user.entity.Role;
import com.pgfinder.user.entity.User;
import com.pgfinder.user.exception.EmailAlreadyExistsException;
import com.pgfinder.user.exception.UserNotFoundException;
import com.pgfinder.user.mapper.UserMapper;
import com.pgfinder.user.repository.UserRepository;
import com.pgfinder.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepo;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepo,
                           UserMapper userMapper) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
    }



    @Override
    public UserResponseDto createUser(UserRequestDto userRequest) {
        if (userRepo.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists: " + userRequest.getEmail()
            );
        }
        User user = userMapper.toEntity(userRequest);
        user.setRole(Role.USER);
        User savedUser = userRepo.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepo.findAll().stream().map(userMapper::toResponse).toList();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found " + id));

        return userMapper.toResponse(user);    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequest) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found " + id));

        existingUser.setName(userRequest.getName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPhone(userRequest.getPhone());

        User updatedUser = userRepo.save(existingUser);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found " + id));

        userRepo.delete(user);

    }

}
