package com.pgfinder.user.controller;

import com.pgfinder.user.dto.UserRequestDto;
import com.pgfinder.user.dto.UserResponseDto;
import com.pgfinder.user.entity.Role;
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


    // 1. View My Own Profile (Any authenticated role: USER, OWNER, ADMIN)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyProfile(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }


    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto userRequest) {

        UserResponseDto response = userService.createUser(userRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Only ADMIN can view all users");
        }
        return ResponseEntity.ok(userService.getAllUsers());
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
    // 3. Promote/Change Role (ADMIN ONLY)
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role newRole,
            @RequestHeader(value = "X-User-Role", required = false) String callerRole) {
        if (!"ADMIN".equalsIgnoreCase(callerRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Only an ADMIN can change user roles");
        }
        UserResponseDto updatedUser = userService.getUserById(id);
        // Update role in DB via repository or service
        return ResponseEntity.ok("User " + id + " promoted to " + newRole);
    }
}