package com.pgfinder.user.controller;

import com.pgfinder.user.dto.AuthResponseDto;
import com.pgfinder.user.dto.LoginRequestDto;
import com.pgfinder.user.dto.UserRequestDto;
import com.pgfinder.user.dto.UserResponseDto;
import com.pgfinder.user.entity.Role;
import com.pgfinder.user.entity.User;
import com.pgfinder.user.exception.EmailAlreadyExistsException;
import com.pgfinder.user.mapper.UserMapper;
import com.pgfinder.user.repository.UserRepository;
import com.pgfinder.user.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          UserMapper userMapper,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid@RequestBody UserRequestDto request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists : "+ request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Security check: Only allow USER or OWNER from public signup
        if (request.getRole() != null && request.getRole() == Role.OWNER) {
            user.setRole(Role.OWNER);
        } else {
            user.setRole(Role.USER); // Default to USER
        }

        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(userMapper.toResponse(savedUser), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        AuthResponseDto response = AuthResponseDto.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
        return ResponseEntity.ok(response);
    }
}
