package com.pgfinder.user.controller;

import com.pgfinder.user.dto.UserRequestDto;
import com.pgfinder.user.dto.UserResponseDto;
import com.pgfinder.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto userRequest) {

        UserResponseDto response = userService.createUser(userRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        List<UserResponseDto> users = userService.getAllUsers();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long id) {

        UserResponseDto user = userService.getUserById(id);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequest) {

        UserResponseDto response =
                userService.updateUser(id, userRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return new ResponseEntity<>(
                "User deleted successfully",
                HttpStatus.OK
        );
    }
}